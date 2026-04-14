package tn.educanet.pfe.serviceimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import tn.educanet.pfe.api.dto.CarnetNumeriqueUploadRequest;
import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.persistence.EleveCarnetNumerique;
import tn.educanet.pfe.repository.EleveCarnetNumeriqueRepository;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.service.EleveCarnetNumeriqueService;

@Service
public class EleveCarnetNumeriqueServiceImpl implements EleveCarnetNumeriqueService {

	private static final int MAX_BYTES = 6 * 1024 * 1024;

	private final EleveRepository eleveRepository;
	private final EleveCarnetNumeriqueRepository carnetNumeriqueRepository;

	public EleveCarnetNumeriqueServiceImpl(EleveRepository eleveRepository,
			EleveCarnetNumeriqueRepository carnetNumeriqueRepository) {
		this.eleveRepository = eleveRepository;
		this.carnetNumeriqueRepository = carnetNumeriqueRepository;
	}

	@Value("${app.carnet-numerique.dir:uploads/carnet-numerique}")
	private String storageDirProperty;

	private Path storageDir;

	@PostConstruct
	void init() throws IOException {
		storageDir = Path.of(storageDirProperty).toAbsolutePath().normalize();
		Files.createDirectories(storageDir);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasImage(Long eleveId) {
		return carnetNumeriqueRepository.existsByEleveId(eleveId);
	}

	@Override
	@Transactional(readOnly = true)
	public String getContentType(Long eleveId) {
		return carnetNumeriqueRepository.findByEleveId(eleveId).map(EleveCarnetNumerique::getContentType)
				.orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
	}

	@Override
	@Transactional(readOnly = true)
	public byte[] getImageBytes(Long eleveId) {
		EleveCarnetNumerique meta = carnetNumeriqueRepository.findByEleveId(eleveId)
				.orElseThrow(() -> new BusinessException("Aucune image de carnet numérique pour cet élève."));
		Path file = storageDir.resolve(meta.getStoredFilename());
		if (!Files.isRegularFile(file)) {
			throw new BusinessException("Fichier image introuvable sur le serveur.");
		}
		try {
			return Files.readAllBytes(file);
		} catch (IOException e) {
			throw new BusinessException("Lecture du fichier impossible.");
		}
	}

	@Override
	@Transactional
	public void upload(Long eleveId, CarnetNumeriqueUploadRequest request) {
		Eleve eleve = eleveRepository.findById(eleveId).orElseThrow(() -> new BusinessException("Élève introuvable"));
		byte[] raw = decodeImage(request.getImage());
		detectAllowedImage(raw);
		String ext = extensionFromContent(raw);
		String ct = contentTypeForExt(ext);

		String filename = eleveId + "_" + UUID.randomUUID() + ext;
		Path target = storageDir.resolve(filename);

		carnetNumeriqueRepository.findByEleveId(eleveId).ifPresent(old -> {
			try {
				Files.deleteIfExists(storageDir.resolve(old.getStoredFilename()));
			} catch (IOException ignored) {
			}
			carnetNumeriqueRepository.delete(old);
		});

		try {
			Files.write(target, raw);
		} catch (IOException e) {
			throw new BusinessException("Enregistrement du fichier impossible.");
		}

		EleveCarnetNumerique row = new EleveCarnetNumerique();
		row.setEleve(eleve);
		row.setStoredFilename(filename);
		row.setContentType(ct);
		row.setUpdatedAt(Instant.now());
		carnetNumeriqueRepository.save(row);
	}

	@Override
	@Transactional
	public void supprimer(Long eleveId) {
		supprimerSiPresent(eleveId);
	}

	@Override
	@Transactional
	public void supprimerSiPresent(Long eleveId) {
		carnetNumeriqueRepository.findByEleveId(eleveId).ifPresent(meta -> {
			try {
				Files.deleteIfExists(storageDir.resolve(meta.getStoredFilename()));
			} catch (IOException ignored) {
			}
			carnetNumeriqueRepository.delete(meta);
		});
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
		// JPEG
		if (raw.length >= 3 && raw[0] == (byte) 0xff && raw[1] == (byte) 0xd8 && raw[2] == (byte) 0xff) {
			return;
		}
		// PNG
		if (raw.length >= 8 && raw[0] == (byte) 0x89 && raw[1] == 'P' && raw[2] == 'N' && raw[3] == 'G') {
			return;
		}
		// GIF
		if (raw.length >= 6 && raw[0] == 'G' && raw[1] == 'I' && raw[2] == 'F') {
			return;
		}
		// WEBP RIFF....WEBP
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
