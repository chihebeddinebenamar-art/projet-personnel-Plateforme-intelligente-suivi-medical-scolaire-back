package tn.educanet.pfe.service;

import java.util.List;

import tn.educanet.pfe.api.dto.TypeVaccinDto;
import tn.educanet.pfe.api.dto.TypeVaccinRequest;

public interface TypeVaccinService {

	List<TypeVaccinDto> lister();

	TypeVaccinDto creer(TypeVaccinRequest request);

	TypeVaccinDto modifier(Long id, TypeVaccinRequest request);

	void supprimer(Long id);
}
