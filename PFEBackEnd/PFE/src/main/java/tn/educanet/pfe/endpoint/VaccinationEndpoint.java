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
import tn.educanet.pfe.api.dto.VaccinationDto;
import tn.educanet.pfe.api.dto.VaccinationRequest;
import tn.educanet.pfe.service.VaccinationService;

@RestController
@RequestMapping("/api/vaccinations")
public class VaccinationEndpoint {

	private final VaccinationService vaccinationService;

	public VaccinationEndpoint(VaccinationService vaccinationService) {
		this.vaccinationService = vaccinationService;
	}

	/**
	 * Sans {@code eleveId} : liste globale avec filtres optionnels (niveau, classe, type, q nom/prénom/matricule, numeroLot).
	 * Avec {@code eleveId} : vaccinations d’un élève (comportement historique).
	 */
	@GetMapping
	public List<VaccinationDto> lister(@RequestParam(required = false) Long eleveId,
			@RequestParam(required = false) Long niveauId, @RequestParam(required = false) Long classeId,
			@RequestParam(required = false) Long typeVaccinId, @RequestParam(required = false) String q,
			@RequestParam(required = false) String numeroLot) {
		if (eleveId != null) {
			return vaccinationService.listerParEleve(eleveId);
		}
		return vaccinationService.listerFiltres(niveauId, classeId, typeVaccinId, q, numeroLot);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public VaccinationDto creer(@Valid @RequestBody VaccinationRequest request) {
		return vaccinationService.creer(request);
	}

	@PutMapping("/{id}")
	public VaccinationDto modifier(@PathVariable Long id, @Valid @RequestBody VaccinationRequest request) {
		return vaccinationService.modifier(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void supprimer(@PathVariable Long id) {
		vaccinationService.supprimer(id);
	}
}
