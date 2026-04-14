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
import tn.educanet.pfe.api.dto.NiveauDto;
import tn.educanet.pfe.api.dto.NiveauRequest;
import tn.educanet.pfe.service.NiveauService;

@RestController
@RequestMapping("/api/niveaux")
public class NiveauEndpoint {

	private final NiveauService niveauService;

	public NiveauEndpoint(NiveauService niveauService) {
		this.niveauService = niveauService;
	}

	@GetMapping
	public List<NiveauDto> lister(@RequestParam(required = false) String annee,
			@RequestParam(required = false) String nom) {
		return niveauService.lister(annee, nom);
	}

	@GetMapping("/{id}")
	public NiveauDto get(@PathVariable Long id) {
		return niveauService.get(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public NiveauDto creer(@Valid @RequestBody NiveauRequest request) {
		return niveauService.creer(request);
	}

	@PutMapping("/{id}")
	public NiveauDto modifier(@PathVariable Long id, @Valid @RequestBody NiveauRequest request) {
		return niveauService.modifier(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void supprimer(@PathVariable Long id) {
		niveauService.supprimer(id);
	}
}
