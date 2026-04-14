package tn.educanet.pfe.endpoint;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tn.educanet.pfe.api.dto.MaladieEleveListDto;
import tn.educanet.pfe.service.EleveMaladieService;

/**
 * Liste globale des allergies / maladies chroniques (tous élèves), avec filtres.
 */
@RestController
@RequestMapping("/api/maladies")
public class MaladiesEndpoint {

	private final EleveMaladieService eleveMaladieService;

	public MaladiesEndpoint(EleveMaladieService eleveMaladieService) {
		this.eleveMaladieService = eleveMaladieService;
	}

	@GetMapping
	public List<MaladieEleveListDto> lister(@RequestParam(required = false) Long niveauId,
			@RequestParam(required = false) Long classeId, @RequestParam(required = false) String q) {
		return eleveMaladieService.listerFiltres(niveauId, classeId, q);
	}
}
