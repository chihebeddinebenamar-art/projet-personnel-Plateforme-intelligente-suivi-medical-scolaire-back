package tn.educanet.pfe.service;

import java.util.List;

import tn.educanet.pfe.api.dto.MaladieEleveDto;
import tn.educanet.pfe.api.dto.MaladieEleveListDto;
import tn.educanet.pfe.api.dto.MaladieEleveRequest;

public interface EleveMaladieService {

	List<MaladieEleveListDto> listerFiltres(Long niveauId, Long classeId, String q);

	List<MaladieEleveDto> lister(Long eleveId);

	MaladieEleveDto creer(Long eleveId, MaladieEleveRequest request);

	MaladieEleveDto modifier(Long eleveId, Long id, MaladieEleveRequest request);

	void supprimer(Long eleveId, Long id);
}
