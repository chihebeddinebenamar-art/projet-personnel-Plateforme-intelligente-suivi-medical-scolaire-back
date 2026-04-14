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
import tn.educanet.pfe.api.dto.ClasseDto;
import tn.educanet.pfe.api.dto.ClasseRequest;
import tn.educanet.pfe.service.ClasseService;

@RestController
@RequestMapping("/api/classes")
public class ClasseEndpoint {

	private final ClasseService classeService;

	public ClasseEndpoint(ClasseService classeService) {
		this.classeService = classeService;
	}

	@GetMapping
	public List<ClasseDto> lister(@RequestParam(required = false) Long niveauId,
			@RequestParam(required = false) String nom) {
		return classeService.lister(niveauId, nom);
	}

	@GetMapping("/by-niveau/{idNiveau}")
	public List<ClasseDto> parNiveau(@PathVariable("idNiveau") Long idNiveau) {
		return classeService.parNiveau(idNiveau);
	}

	@GetMapping("/{id}")
	public ClasseDto get(@PathVariable Long id) {
		return classeService.get(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ClasseDto creer(@Valid @RequestBody ClasseRequest request) {
		return classeService.creer(request);
	}

	@PutMapping("/{id}")
	public ClasseDto modifier(@PathVariable Long id, @Valid @RequestBody ClasseRequest request) {
		return classeService.modifier(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void supprimer(@PathVariable Long id) {
		classeService.supprimer(id);
	}
}
