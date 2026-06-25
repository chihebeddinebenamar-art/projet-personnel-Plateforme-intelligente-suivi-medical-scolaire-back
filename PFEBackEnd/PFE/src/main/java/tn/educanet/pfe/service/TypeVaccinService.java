package tn.educanet.pfe.service;

import java.util.List;

import com.tn.educanet.pfe.api.vaccins.types.schema.TypeVaccinDto;
import com.tn.educanet.pfe.api.vaccins.types.schema.TypeVaccinRequest;

public interface TypeVaccinService {

	List<TypeVaccinDto> lister();

	TypeVaccinDto creer(TypeVaccinRequest request);

	TypeVaccinDto modifier(Long id, TypeVaccinRequest request);

	void supprimer(Long id);
}
