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
import tn.educanet.pfe.api.dto.AccidentDto;
import tn.educanet.pfe.api.dto.AccidentRequest;
import tn.educanet.pfe.service.AccidentService;

@RestController
@RequestMapping("/api/accidents")
public class AccidentEndpoint {

	private final AccidentService accidentService;

	public AccidentEndpoint(AccidentService accidentService) {
		this.accidentService = accidentService;
	}

	/**
	 * Sans {@code eleveId} : liste globale avec filtres optionnels (niveau, classe, recherche nom/prénom/matricule).
	 * Avec {@code eleveId} : accidents d’un élève (comportement historique).
	 */
	@GetMapping
	public List<AccidentDto> lister(@RequestParam(required = false) Long eleveId,
			@RequestParam(required = false) Long niveauId, @RequestParam(required = false) Long classeId,
			@RequestParam(required = false) String q) {
		if (eleveId != null) {
			return accidentService.listerParEleve(eleveId);
		}
		return accidentService.listerFiltres(niveauId, classeId, q);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public AccidentDto creer(@Valid @RequestBody AccidentRequest request) {
		return accidentService.creer(request);
	}

	@PutMapping("/{id}")
	public AccidentDto modifier(@PathVariable Long id, @Valid @RequestBody AccidentRequest request) {
		return accidentService.modifier(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void supprimer(@PathVariable Long id) {
		accidentService.supprimer(id);
	}
}
