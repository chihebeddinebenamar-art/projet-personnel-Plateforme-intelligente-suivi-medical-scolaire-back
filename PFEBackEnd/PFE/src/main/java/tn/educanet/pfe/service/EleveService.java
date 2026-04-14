package tn.educanet.pfe.service;

import java.util.List;

import tn.educanet.pfe.api.dto.EleveDto;
import tn.educanet.pfe.api.dto.EleveRequest;
import tn.educanet.pfe.api.dto.FicheMedicaleDto;

public interface EleveService {

	List<EleveDto> listerFiltres(Long niveauId, Long classeId, String recherche);

	List<EleveDto> parClasse(Long classeId);

	EleveDto creer(EleveRequest request);

	EleveDto modifier(Long id, EleveRequest request);

	void supprimer(Long id);

	EleveDto get(Long id);

	FicheMedicaleDto ficheMedicale(Long eleveId);
}
