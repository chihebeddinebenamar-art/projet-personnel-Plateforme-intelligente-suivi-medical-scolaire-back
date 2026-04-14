package tn.educanet.pfe.endpoint;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tn.educanet.pfe.api.dto.CarnetNumeriqueUploadRequest;
import tn.educanet.pfe.service.EleveCarnetNumeriqueService;

@RestController
@RequestMapping("/api/eleves/{eleveId}/carnet-numerique")
public class EleveCarnetNumeriqueEndpoint {

	private final EleveCarnetNumeriqueService eleveCarnetNumeriqueService;

	public EleveCarnetNumeriqueEndpoint(EleveCarnetNumeriqueService eleveCarnetNumeriqueService) {
		this.eleveCarnetNumeriqueService = eleveCarnetNumeriqueService;
	}

	@GetMapping("/image")
	public ResponseEntity<byte[]> image(@PathVariable Long eleveId) {
		if (!eleveCarnetNumeriqueService.hasImage(eleveId)) {
			return ResponseEntity.notFound().build();
		}
		byte[] body = eleveCarnetNumeriqueService.getImageBytes(eleveId);
		String ct = eleveCarnetNumeriqueService.getContentType(eleveId);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(ct))
				.cacheControl(CacheControl.noCache().mustRevalidate()).header(HttpHeaders.PRAGMA, "no-cache").body(body);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void upload(@PathVariable Long eleveId, @Valid @RequestBody CarnetNumeriqueUploadRequest request) {
		eleveCarnetNumeriqueService.upload(eleveId, request);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void supprimer(@PathVariable Long eleveId) {
		eleveCarnetNumeriqueService.supprimer(eleveId);
	}
}
