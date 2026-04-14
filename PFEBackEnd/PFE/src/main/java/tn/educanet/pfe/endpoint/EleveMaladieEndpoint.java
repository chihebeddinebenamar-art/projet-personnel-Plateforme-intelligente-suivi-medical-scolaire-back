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
import tn.educanet.pfe.api.dto.MaladieEleveDto;
import tn.educanet.pfe.api.dto.MaladieEleveRequest;
import tn.educanet.pfe.service.EleveMaladieService;

@RestController
@RequestMapping("/api/eleves/{eleveId}/maladies")
public class EleveMaladieEndpoint {

	private final EleveMaladieService eleveMaladieService;

	public EleveMaladieEndpoint(EleveMaladieService eleveMaladieService) {
		this.eleveMaladieService = eleveMaladieService;
	}

	@GetMapping
	public List<MaladieEleveDto> lister(@PathVariable Long eleveId) {
		return eleveMaladieService.lister(eleveId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MaladieEleveDto creer(@PathVariable Long eleveId, @Valid @RequestBody MaladieEleveRequest request) {
		return eleveMaladieService.creer(eleveId, request);
	}

	@PutMapping("/{id}")
	public MaladieEleveDto modifier(@PathVariable Long eleveId, @PathVariable Long id,
			@Valid @RequestBody MaladieEleveRequest request) {
		return eleveMaladieService.modifier(eleveId, id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void supprimer(@PathVariable Long eleveId, @PathVariable Long id) {
		eleveMaladieService.supprimer(eleveId, id);
	}
}
