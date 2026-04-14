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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tn.educanet.pfe.api.dto.RappelVaccinationDto;
import tn.educanet.pfe.api.dto.RappelVaccinationRequest;
import tn.educanet.pfe.service.RappelVaccinationService;

@RestController
@RequestMapping("/api/rappels-vaccination")
public class RappelVaccinationEndpoint {

	private final RappelVaccinationService rappelVaccinationService;

	public RappelVaccinationEndpoint(RappelVaccinationService rappelVaccinationService) {
		this.rappelVaccinationService = rappelVaccinationService;
	}

	@GetMapping
	public List<RappelVaccinationDto> lister() {
		return rappelVaccinationService.lister();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RappelVaccinationDto creer(@Valid @RequestBody RappelVaccinationRequest request) {
		return rappelVaccinationService.creer(request);
	}

	@PutMapping("/{id}")
	public RappelVaccinationDto modifier(@PathVariable Long id, @Valid @RequestBody RappelVaccinationRequest request) {
		return rappelVaccinationService.modifier(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void supprimer(@PathVariable Long id) {
		rappelVaccinationService.supprimer(id);
	}
}
