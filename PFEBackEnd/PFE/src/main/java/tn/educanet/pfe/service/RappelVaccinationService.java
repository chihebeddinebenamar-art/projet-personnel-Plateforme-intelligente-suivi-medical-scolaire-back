package tn.educanet.pfe.service;

import java.util.List;

import com.tn.educanet.pfe.api.rappels.vaccination.schema.RappelVaccinationDto;
import com.tn.educanet.pfe.api.rappels.vaccination.schema.RappelVaccinationRequest;

public interface RappelVaccinationService {

	List<RappelVaccinationDto> lister();

	RappelVaccinationDto creer(RappelVaccinationRequest request);

	RappelVaccinationDto modifier(Long id, RappelVaccinationRequest request);

	void supprimer(Long id);
}
