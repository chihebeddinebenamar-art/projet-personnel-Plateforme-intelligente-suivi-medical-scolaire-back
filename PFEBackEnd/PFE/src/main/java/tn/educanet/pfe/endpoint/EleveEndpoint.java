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
import tn.educanet.pfe.api.dto.EleveDto;
import tn.educanet.pfe.api.dto.EleveRequest;
import tn.educanet.pfe.api.dto.FicheMedicaleDto;
import tn.educanet.pfe.service.EleveService;

@RestController
@RequestMapping("/api/eleves")
public class EleveEndpoint {

	private final EleveService eleveService;

	public EleveEndpoint(EleveService eleveService) {
		this.eleveService = eleveService;
	}

	@GetMapping
	public List<EleveDto> lister(@RequestParam(required = false) Long niveauId,
			@RequestParam(required = false) Long classeId, @RequestParam(required = false) String q) {
		return eleveService.listerFiltres(niveauId, classeId, q);
	}

	@GetMapping("/by-classe/{idClasse}")
	public List<EleveDto> parClasse(@PathVariable("idClasse") Long idClasse) {
		return eleveService.parClasse(idClasse);
	}

	@GetMapping("/{id}/fiche-medicale")
	public FicheMedicaleDto ficheMedicale(@PathVariable Long id) {
		return eleveService.ficheMedicale(id);
	}

	@GetMapping("/{id}")
	public EleveDto get(@PathVariable Long id) {
		return eleveService.get(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EleveDto creer(@Valid @RequestBody EleveRequest request) {
		return eleveService.creer(request);
	}

	@PutMapping("/{id}")
	public EleveDto modifier(@PathVariable Long id, @Valid @RequestBody EleveRequest request) {
		return eleveService.modifier(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void supprimer(@PathVariable Long id) {
		eleveService.supprimer(id);
	}
}
