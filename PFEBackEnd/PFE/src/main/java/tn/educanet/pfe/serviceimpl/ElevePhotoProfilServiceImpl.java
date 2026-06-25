package tn.educanet.pfe.serviceimpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.persistence.ElevePhotoProfil;
import tn.educanet.pfe.repository.ElevePhotoProfilRepository;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.service.ElevePhotoProfilService;

@Service
public class ElevePhotoProfilServiceImpl implements ElevePhotoProfilService {

	private static final int MAX_BYTES = 6 * 1024 * 1024;

	private final EleveRepository eleveRepository;
	private final ElevePhotoProfilRepository photoProfilRepository;
	private final Cloudinary cloudinary;

	public ElevePhotoProfilServiceImpl(EleveRepository eleveRepository, ElevePhotoProfilRepository photoProfilRepository,
			Cloudinary cloudinary) {
		this.eleveRepository = eleveRepository;
		this.photoProfilRepository = photoProfilRepository;
		this.cloudinary = cloudinary;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasImage(Long eleveId) {
		return photoProfilRepository.existsByEleveId(eleveId);
	}

	@Override
	@Transactional(readOnly = true)
	public String getContentType(Long eleveId) {
		return photoProfilRepository.findByEleveId(eleveId).map(ElevePhotoProfil::getContentType)
				.orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
	}

	@Override
	@Transactional(readOnly = true)
	public byte[] getImageBytes(Long eleveId) {
		ElevePhotoProfil meta = photoProfilRepository.findByEleveId(eleveId)
				.orElseThrow(() -> new BusinessException("Aucune photo de profil pour cet élève."));
		try {
			String imageUrl = cloudinary.url().secure(true).publicId(meta.getStoredFilename()).generate();
			try (InputStream in = new URL(imageUrl).openStream()) {
				return in.readAllBytes();
			}
		} catch (IOException e) {
			throw new BusinessException("Lecture de l'image Cloudinary impossible.");
		}
	}

	@Override
	@Transactional
	public void upload(Long eleveId, String imageBase64) {
		Eleve eleve = eleveRepository.findById(eleveId).orElseThrow(() -> new BusinessException("Élève introuvable"));
		byte[] raw = decodeImage(imageBase64);
		detectAllowedImage(raw);
		String ext = extensionFromContent(raw);
		String ct = contentTypeForExt(ext);
		String folder = "educanet/eleves/photo-profil";
		String publicId;
		ElevePhotoProfil row = photoProfilRepository.findByEleveId(eleveId).orElse(null);
		String oldPublicId = row != null ? row.getStoredFilename() : null;

		try {
			@SuppressWarnings("rawtypes")
			Map uploadResult = cloudinary.uploader().upload(raw, ObjectUtils.asMap("folder", folder, "resource_type", "image"));
			publicId = String.valueOf(uploadResult.get("public_id"));
		} catch (Exception e) {
			throw new BusinessException("Upload Cloudinary impossible.");
		}

		if (row == null) {
			row = new ElevePhotoProfil();
			row.setEleve(eleve);
		}
		row.setStoredFilename(publicId);
		row.setContentType(ct);
		row.setUpdatedAt(Instant.now());
		photoProfilRepository.save(row);
		if (oldPublicId != null && !oldPublicId.equals(publicId)) {
			destroyCloudinaryAssetQuietly(oldPublicId);
		}
	}

	@Override
	@Transactional
	public void supprimer(Long eleveId) {
		supprimerSiPresent(eleveId);
	}

	@Override
	@Transactional
	public void supprimerSiPresent(Long eleveId) {
		photoProfilRepository.findByEleveId(eleveId).ifPresent(meta -> {
			destroyCloudinaryAssetQuietly(meta.getStoredFilename());
			photoProfilRepository.delete(meta);
		});
	}

	private void destroyCloudinaryAssetQuietly(String publicId) {
		try {
			cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
		} catch (Exception ignored) {
		}
	}

	private static byte[] decodeImage(String input) {
		String s = input == null ? "" : input.trim();
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
		if (raw.length >= 2 && raw[0] == 'B' && raw[1] == 'M') {
			return;
		}
		throw new BusinessException("Format non supporté (JPEG, PNG, GIF, WebP ou BMP uniquement).");
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
		if (raw.length >= 2 && raw[0] == 'B' && raw[1] == 'M') {
			return ".bmp";
		}
		return ".webp";
	}

	private static String contentTypeForExt(String ext) {
		return switch (ext) {
			case ".png" -> MediaType.IMAGE_PNG_VALUE;
			case ".gif" -> MediaType.IMAGE_GIF_VALUE;
			case ".webp" -> "image/webp";
			case ".bmp" -> "image/bmp";
			default -> MediaType.IMAGE_JPEG_VALUE;
		};
	}
}
