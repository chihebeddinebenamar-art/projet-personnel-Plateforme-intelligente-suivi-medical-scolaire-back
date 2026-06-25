package tn.educanet.pfe.service;

import java.util.List;

import com.tn.educanet.pfe.api.consultations.schema.ConsultationDto;
import com.tn.educanet.pfe.api.consultations.schema.ConsultationRequest;

public interface ConsultationService {

	List<ConsultationDto> listerParEleve(Long eleveId);

	List<ConsultationDto> listerFiltres(Long niveauId, Long classeId, String q);

	ConsultationDto creer(ConsultationRequest request);

	ConsultationDto modifier(Long id, ConsultationRequest request);

	void supprimer(Long id);
}
