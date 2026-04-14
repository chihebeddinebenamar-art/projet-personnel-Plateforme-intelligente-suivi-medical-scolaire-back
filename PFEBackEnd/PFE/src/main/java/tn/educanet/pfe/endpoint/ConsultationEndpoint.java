package tn.educanet.pfe.endpoint;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tn.educanet.pfe.api.dto.ConsultationDto;
import tn.educanet.pfe.api.dto.ConsultationRequest;
import tn.educanet.pfe.service.ConsultationService;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationEndpoint {

	private final ConsultationService consultationService;

	public ConsultationEndpoint(ConsultationService consultationService) {
		this.consultationService = consultationService;
	}

	/**
	 * Sans {@code eleveId} : liste globale avec filtres optionnels (niveau, classe, recherche nom/prénom/matricule).
	 * Avec {@code eleveId} : consultations d’un élève (comportement historique).
	 */
	@GetMapping
	public List<ConsultationDto> lister(@RequestParam(required = false) Long eleveId,
			@RequestParam(required = false) Long niveauId, @RequestParam(required = false) Long classeId,
			@RequestParam(required = false) String q) {
		if (eleveId != null) {
			return consultationService.listerParEleve(eleveId);
		}
		return consultationService.listerFiltres(niveauId, classeId, q);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ConsultationDto creer(@Valid @RequestBody ConsultationRequest request) {
		return consultationService.creer(request);
	}

	@PutMapping("/{id}")
	public ConsultationDto modifier(@PathVariable Long id, @Valid @RequestBody ConsultationRequest request) {
		return consultationService.modifier(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void supprimer(@PathVariable Long id) {
		consultationService.supprimer(id);
	}
}
