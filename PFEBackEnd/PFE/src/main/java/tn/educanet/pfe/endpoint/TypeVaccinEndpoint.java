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
import tn.educanet.pfe.api.dto.TypeVaccinDto;
import tn.educanet.pfe.api.dto.TypeVaccinRequest;
import tn.educanet.pfe.service.TypeVaccinService;

@RestController
@RequestMapping("/api/vaccins/types")
public class TypeVaccinEndpoint {

	private final TypeVaccinService typeVaccinService;

	public TypeVaccinEndpoint(TypeVaccinService typeVaccinService) {
		this.typeVaccinService = typeVaccinService;
	}

	@GetMapping
	public List<TypeVaccinDto> lister() {
		return typeVaccinService.lister();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TypeVaccinDto creer(@Valid @RequestBody TypeVaccinRequest request) {
		return typeVaccinService.creer(request);
	}

	@PutMapping("/{id}")
	public TypeVaccinDto modifier(@PathVariable Long id, @Valid @RequestBody TypeVaccinRequest request) {
		return typeVaccinService.modifier(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void supprimer(@PathVariable Long id) {
		typeVaccinService.supprimer(id);
	}
}
