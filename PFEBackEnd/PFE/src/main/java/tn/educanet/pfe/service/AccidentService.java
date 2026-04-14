package tn.educanet.pfe.service;

import java.util.List;

import tn.educanet.pfe.api.dto.AccidentDto;
import tn.educanet.pfe.api.dto.AccidentRequest;

public interface AccidentService {

	List<AccidentDto> listerParEleve(Long eleveId);

	List<AccidentDto> listerFiltres(Long niveauId, Long classeId, String q);

	AccidentDto creer(AccidentRequest request);

	AccidentDto modifier(Long id, AccidentRequest request);

	void supprimer(Long id);
}
