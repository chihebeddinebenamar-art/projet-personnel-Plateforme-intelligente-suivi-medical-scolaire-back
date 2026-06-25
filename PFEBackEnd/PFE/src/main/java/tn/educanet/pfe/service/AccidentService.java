package tn.educanet.pfe.service;

import java.util.List;

import com.tn.educanet.pfe.api.accidents.schema.AccidentDto;
import com.tn.educanet.pfe.api.accidents.schema.AccidentRequest;

public interface AccidentService {

	List<AccidentDto> listerParEleve(Long eleveId);

	List<AccidentDto> listerFiltres(Long niveauId, Long classeId, String q);

	AccidentDto creer(AccidentRequest request);

	AccidentDto modifier(Long id, AccidentRequest request);

	void supprimer(Long id);
}
