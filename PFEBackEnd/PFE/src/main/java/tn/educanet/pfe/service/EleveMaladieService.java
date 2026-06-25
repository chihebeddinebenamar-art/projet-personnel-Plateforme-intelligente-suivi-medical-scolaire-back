package tn.educanet.pfe.service;

import java.util.List;

import com.tn.educanet.pfe.api.eleves.maladies.schema.MaladieEleveDto;
import com.tn.educanet.pfe.api.eleves.maladies.schema.MaladieEleveRequest;
import com.tn.educanet.pfe.api.maladies.schema.MaladieEleveListDto;

public interface EleveMaladieService {

	List<MaladieEleveListDto> listerFiltres(Long niveauId, Long classeId, String q);

	List<MaladieEleveDto> lister(Long eleveId);

	MaladieEleveDto creer(Long eleveId, MaladieEleveRequest request);

	MaladieEleveDto modifier(Long eleveId, Long id, MaladieEleveRequest request);

	void supprimer(Long eleveId, Long id);
}
