package tn.educanet.pfe.service;

import java.util.List;

import tn.educanet.pfe.api.dto.RappelVaccinationDto;
import tn.educanet.pfe.api.dto.RappelVaccinationRequest;

public interface RappelVaccinationService {

	List<RappelVaccinationDto> lister();

	RappelVaccinationDto creer(RappelVaccinationRequest request);

	RappelVaccinationDto modifier(Long id, RappelVaccinationRequest request);

	void supprimer(Long id);
}
