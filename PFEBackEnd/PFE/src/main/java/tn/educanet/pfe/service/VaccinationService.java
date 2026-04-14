package tn.educanet.pfe.service;

import java.util.List;

import tn.educanet.pfe.api.dto.VaccinationDto;
import tn.educanet.pfe.api.dto.VaccinationRequest;

public interface VaccinationService {

	List<VaccinationDto> listerParEleve(Long eleveId);

	List<VaccinationDto> listerFiltres(Long niveauId, Long classeId, Long typeVaccinId, String q, String numeroLot);

	VaccinationDto creer(VaccinationRequest request);

	VaccinationDto modifier(Long id, VaccinationRequest request);

	void supprimer(Long id);
}
