package tn.educanet.pfe.serviceimpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.persistence.EleveCarnetNumerique;
import tn.educanet.pfe.persistence.EleveCarnetNumeriquePhoto;
import tn.educanet.pfe.repository.EleveCarnetNumeriquePhotoRepository;
import tn.educanet.pfe.repository.EleveCarnetNumeriqueRepository;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.service.EleveCarnetNumeriqueService;

@Service
public class EleveCarnetNumeriqueServiceImpl implements EleveCarnetNumeriqueService {

	private static final int MAX_BYTES = 6 * 1024 * 1024;

	/**
	 * Valeurs sur la ligne {@link EleveCarnetNumerique} lorsqu’aucun fichier n’y est stocké (images dans
	 * {@link EleveCarnetNumeriquePhoto} uniquement, ou ligne créée avant upload). Évite INSERT/UPDATE avec NULL si les
	 * colonnes SQL {@code stored_filename} / {@code content_type} sont NOT NULL.
	 */
	private static final String META_ROW_NO_STORED_FILENAME = "";

	private static final String META_ROW_NO_FILE_CONTENT_TYPE = MediaType.APPLICATION_OCTET_STREAM_VALUE;

	private final EleveRepository eleveRepository;
	private final EleveCarnetNumeriqueRepository carnetNumeriqueRepository;
	private final EleveCarnetNumeriquePhotoRepository carnetPhotoRepository;
	private final Cloudinary cloudinary;

	public EleveCarnetNumeriqueServiceImpl(EleveRepository eleveRepository,
			EleveCarnetNumeriqueRepository carnetNumeriqueRepository,
			EleveCarnetNumeriquePhotoRepository carnetPhotoRepository, Cloudinary cloudinary) {
		this.eleveRepository = eleveRepository;
		this.carnetNumeriqueRepository = carnetNumeriqueRepository;
		this.carnetPhotoRepository = carnetPhotoRepository;
		this.cloudinary = cloudinary;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasImage(Long eleveId) {
		return carnetPhotoRepository.countByEleve_Id(eleveId) > 0
				|| carnetNumeriqueRepository.findByEleveId(eleveId)
						.map(m -> StringUtils.hasText(m.getStoredFilename()))
						.orElse(false);
	}

	@Override
	@Transactional(readOnly = true)
	public String getContentType(Long eleveId) {
		List<EleveCarnetNumeriquePhoto> photos = carnetPhotoRepository.findByEleve_IdOrderBySortIndexAsc(eleveId);
		if (!photos.isEmpty()) {
			return photos.get(0).getContentType();
		}
		return carnetNumeriqueRepository.findByEleveId(eleveId).map(EleveCarnetNumerique::getContentType)
				.filter(StringUtils::hasText).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
	}

	@Override
	@Transactional(readOnly = true)
	public byte[] getImageBytes(Long eleveId) {
		return getImageBytes(eleveId, null);
	}

	@Override
	@Transactional(readOnly = true)
	public byte[] getImageBytes(Long eleveId, Long photoId) {
		if (photoId != null) {
			EleveCarnetNumeriquePhoto ph = carnetPhotoRepository.findById(photoId)
					.orElseThrow(() -> new BusinessException("Photo du carnet introuvable."));
			if (ph.getEleve() == null || ph.getEleve().getId() == null || !ph.getEleve().getId().equals(eleveId)) {
				throw new BusinessException("Photo du carnet introuvable.");
			}
			return readCloudinaryBytes(ph.getStoredFilename());
		}
		List<EleveCarnetNumeriquePhoto> photos = carnetPhotoRepository.findByEleve_IdOrderBySortIndexAsc(eleveId);
		if (!photos.isEmpty()) {
			return readCloudinaryBytes(photos.get(0).getStoredFilename());
		}
		EleveCarnetNumerique meta = carnetNumeriqueRepository.findByEleveId(eleveId)
				.orElseThrow(() -> new BusinessException("Aucune image de carnet numérique pour cet élève."));
		if (!StringUtils.hasText(meta.getStoredFilename())) {
			throw new BusinessException("Aucune image de carnet numérique pour cet élève.");
		}
		return readCloudinaryBytes(meta.getStoredFilename());
	}

	@Override
	@Transactional
	public List<Long> listPhotoIds(Long eleveId) {
		syncLegacyCarnetPhotos(eleveId);
		return carnetPhotoRepository.findByEleve_IdOrderBySortIndexAsc(eleveId).stream().map(EleveCarnetNumeriquePhoto::getId)
				.toList();
	}

	@Override
	@Transactional
	public void syncLegacyCarnetPhotos(Long eleveId) {
		migrateLegacyPhotoIfNeeded(eleveId);
	}

	@Override
	@Transactional(readOnly = true)
	public String getDescription(Long eleveId) {
		return carnetNumeriqueRepository.findByEleveId(eleveId).map(EleveCarnetNumerique::getDescription).orElse("");
	}

	@Override
	@Transactional(readOnly = true)
	public String resolveContentType(Long eleveId, Long photoId) {
		if (photoId != null) {
			EleveCarnetNumeriquePhoto ph = carnetPhotoRepository.findById(photoId)
					.orElseThrow(() -> new BusinessException("Photo du carnet introuvable."));
			if (ph.getEleve() == null || ph.getEleve().getId() == null || !ph.getEleve().getId().equals(eleveId)) {
				throw new BusinessException("Photo du carnet introuvable.");
			}
			return ph.getContentType();
		}
		return getContentType(eleveId);
	}

	@Override
	@Transactional
	public void upload(Long eleveId, String imageBase64, String description) {
		Eleve eleve = eleveRepository.findById(eleveId).orElseThrow(() -> new BusinessException("Élève introuvable"));
		syncLegacyCarnetPhotos(eleveId);

		if (!StringUtils.hasText(imageBase64)) {
			EleveCarnetNumerique meta = carnetNumeriqueRepository.findByEleveId(eleveId).orElse(null);
			if (meta == null && carnetPhotoRepository.countByEleve_Id(eleveId) == 0) {
				throw new BusinessException("Ajoutez une photo du carnet (première fois).");
			}
			if (meta == null) {
				meta = ensureMeta(eleve);
			}
			if (description != null) {
				meta.setDescription(description);
			}
			meta.setUpdatedAt(Instant.now());
			carnetNumeriqueRepository.save(meta);
			return;
		}

		byte[] raw = decodeImage(imageBase64);
		detectAllowedImage(raw);
		String ext = extensionFromContent(raw);
		String ct = contentTypeForExt(ext);
		String folder = "educanet/eleves/carnet-numerique";

		EleveCarnetNumerique meta = ensureMeta(eleve);
		try {
			@SuppressWarnings("rawtypes")
			Map uploadResult = cloudinary.uploader().upload(raw, ObjectUtils.asMap("folder", folder, "resource_type", "image"));
			String publicId = String.valueOf(uploadResult.get("public_id"));

			int nextIndex = (int) carnetPhotoRepository.countByEleve_Id(eleveId);
			EleveCarnetNumeriquePhoto ph = new EleveCarnetNumeriquePhoto();
			ph.setEleve(eleve);
			ph.setSortIndex(nextIndex);
			ph.setStoredFilename(publicId);
			ph.setContentType(ct);
			ph.setUpdatedAt(Instant.now());
			carnetPhotoRepository.save(ph);

			if (description != null) {
				meta.setDescription(description);
			}
			meta.setUpdatedAt(Instant.now());
			carnetNumeriqueRepository.save(meta);
		} catch (Exception e) {
			throw new BusinessException("Upload Cloudinary impossible.");
		}
	}

	@Override
	@Transactional
	public void supprimer(Long eleveId) {
		supprimerSiPresent(eleveId);
	}

	@Override
	@Transactional
	public void supprimerPhoto(Long eleveId, Long photoId) {
		if (photoId == null) {
			throw new BusinessException("Photo du carnet introuvable.");
		}
		syncLegacyCarnetPhotos(eleveId);
		EleveCarnetNumeriquePhoto ph = carnetPhotoRepository.findById(photoId)
				.orElseThrow(() -> new BusinessException("Photo du carnet introuvable."));
		if (ph.getEleve() == null || ph.getEleve().getId() == null || !ph.getEleve().getId().equals(eleveId)) {
			throw new BusinessException("Photo du carnet introuvable.");
		}
		destroyCloudinaryAssetQuietly(ph.getStoredFilename());
		carnetPhotoRepository.delete(ph);
		List<EleveCarnetNumeriquePhoto> remaining = carnetPhotoRepository.findByEleve_IdOrderBySortIndexAsc(eleveId);
		for (int i = 0; i < remaining.size(); i++) {
			EleveCarnetNumeriquePhoto row = remaining.get(i);
			if (row.getSortIndex() != i) {
				row.setSortIndex(i);
				carnetPhotoRepository.save(row);
			}
		}
		carnetNumeriqueRepository.findByEleveId(eleveId).ifPresent(meta -> {
			meta.setUpdatedAt(Instant.now());
			carnetNumeriqueRepository.save(meta);
		});
	}

	@Override
	@Transactional
	public void supprimerSiPresent(Long eleveId) {
		for (EleveCarnetNumeriquePhoto ph : carnetPhotoRepository.findByEleve_IdOrderBySortIndexAsc(eleveId)) {
			destroyCloudinaryAssetQuietly(ph.getStoredFilename());
		}
		carnetPhotoRepository.deleteByEleve_Id(eleveId);
		carnetNumeriqueRepository.findByEleveId(eleveId).ifPresent(meta -> {
			if (StringUtils.hasText(meta.getStoredFilename())) {
				destroyCloudinaryAssetQuietly(meta.getStoredFilename());
			}
			carnetNumeriqueRepository.delete(meta);
		});
	}

	/**
	 * Si une image unique est encore sur la ligne {@link EleveCarnetNumerique}, la copie en première ligne photo
	 * puis vide les colonnes image du parent.
	 */
	private void migrateLegacyPhotoIfNeeded(Long eleveId) {
		if (carnetPhotoRepository.countByEleve_Id(eleveId) > 0) {
			return;
		}
		EleveCarnetNumerique meta = carnetNumeriqueRepository.findByEleveId(eleveId).orElse(null);
		if (meta == null || !StringUtils.hasText(meta.getStoredFilename())) {
			return;
		}
		Eleve eleve = meta.getEleve();
		EleveCarnetNumeriquePhoto ph = new EleveCarnetNumeriquePhoto();
		ph.setEleve(eleve);
		ph.setSortIndex(0);
		ph.setStoredFilename(meta.getStoredFilename());
		ph.setContentType(StringUtils.hasText(meta.getContentType()) ? meta.getContentType() : MediaType.IMAGE_JPEG_VALUE);
		ph.setUpdatedAt(Instant.now());
		carnetPhotoRepository.save(ph);
		meta.setStoredFilename(META_ROW_NO_STORED_FILENAME);
		meta.setContentType(META_ROW_NO_FILE_CONTENT_TYPE);
		meta.setUpdatedAt(Instant.now());
		carnetNumeriqueRepository.save(meta);
	}

	private EleveCarnetNumerique ensureMeta(Eleve eleve) {
		return carnetNumeriqueRepository.findByEleveId(eleve.getId()).orElseGet(() -> {
			EleveCarnetNumerique m = new EleveCarnetNumerique();
			m.setEleve(eleve);
			m.setDescription("");
			m.setStoredFilename(META_ROW_NO_STORED_FILENAME);
			m.setContentType(META_ROW_NO_FILE_CONTENT_TYPE);
			m.setUpdatedAt(Instant.now());
			return carnetNumeriqueRepository.save(m);
		});
	}

	private byte[] readCloudinaryBytes(String publicId) {
		try {
			String imageUrl = cloudinary.url().secure(true).publicId(publicId).generate();
			try (InputStream in = new URL(imageUrl).openStream()) {
				return in.readAllBytes();
			}
		} catch (IOException e) {
			throw new BusinessException("Lecture de l'image Cloudinary impossible.");
		}
	}

	private void destroyCloudinaryAssetQuietly(String publicId) {
		if (!StringUtils.hasText(publicId)) {
			return;
		}
		try {
			cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
		} catch (Exception ignored) {
		}
	}

	private static byte[] decodeImage(String input) {
		String s = input.trim();
		if (s.startsWith("data:")) {
			int comma = s.indexOf(',');
			if (comma < 0) {
				throw new BusinessException("Image base64 invalide (data URL).");
			}
			s = s.substring(comma + 1);
		}
		s = s.replaceAll("\\s", "");
		try {
			byte[] decoded = Base64.getDecoder().decode(s);
			if (decoded.length > MAX_BYTES) {
				throw new BusinessException("Image trop volumineuse (max. 6 Mo).");
			}
			if (decoded.length < 16) {
				throw new BusinessException("Fichier image trop petit ou invalide.");
			}
			return decoded;
		} catch (IllegalArgumentException e) {
			throw new BusinessException("Image base64 invalide.");
		}
	}

	private static void detectAllowedImage(byte[] raw) {
		if (raw.length >= 3 && raw[0] == (byte) 0xff && raw[1] == (byte) 0xd8 && raw[2] == (byte) 0xff) {
			return;
		}
		if (raw.length >= 8 && raw[0] == (byte) 0x89 && raw[1] == 'P' && raw[2] == 'N' && raw[3] == 'G') {
			return;
		}
		if (raw.length >= 6 && raw[0] == 'G' && raw[1] == 'I' && raw[2] == 'F') {
			return;
		}
		if (raw.length >= 12 && raw[0] == 'R' && raw[1] == 'I' && raw[2] == 'F' && raw[3] == 'F'
				&& raw[8] == 'W' && raw[9] == 'E' && raw[10] == 'B' && raw[11] == 'P') {
			return;
		}
		throw new BusinessException("Format non supporté (JPEG, PNG, GIF ou WebP uniquement).");
	}

	private static String extensionFromContent(byte[] raw) {
		if (raw.length >= 3 && raw[0] == (byte) 0xff && raw[1] == (byte) 0xd8) {
			return ".jpg";
		}
		if (raw.length >= 8 && raw[0] == (byte) 0x89 && raw[1] == 'P') {
			return ".png";
		}
		if (raw.length >= 6 && raw[0] == 'G' && raw[1] == 'I') {
			return ".gif";
		}
		return ".webp";
	}

	private static String contentTypeForExt(String ext) {
		return switch (ext) {
			case ".png" -> MediaType.IMAGE_PNG_VALUE;
			case ".gif" -> MediaType.IMAGE_GIF_VALUE;
			case ".webp" -> "image/webp";
			default -> MediaType.IMAGE_JPEG_VALUE;
		};
	}
}
