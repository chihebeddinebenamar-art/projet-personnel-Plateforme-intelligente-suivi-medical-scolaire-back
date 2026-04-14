package tn.educanet.pfe.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.educanet.pfe.api.dto.TypeVaccinDto;
import tn.educanet.pfe.api.dto.TypeVaccinRequest;
import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.TypeVaccin;
import tn.educanet.pfe.repository.VaccinationRepository;
import tn.educanet.pfe.repository.TypeVaccinRepository;
import tn.educanet.pfe.service.TypeVaccinService;

@Service
public class TypeVaccinServiceImpl implements TypeVaccinService {

	private final TypeVaccinRepository typeVaccinRepository;
	private final VaccinationRepository vaccinationRepository;

	public TypeVaccinServiceImpl(TypeVaccinRepository typeVaccinRepository,
			VaccinationRepository vaccinationRepository) {
		this.typeVaccinRepository = typeVaccinRepository;
		this.vaccinationRepository = vaccinationRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TypeVaccinDto> lister() {
		return typeVaccinRepository.findAll().stream().map(TypeVaccinDto::from).toList();
	}

	@Override
	@Transactional
	public TypeVaccinDto creer(TypeVaccinRequest request) {
		TypeVaccin t = new TypeVaccin();
		t.setNom(request.getNom());
		t.setQuantiteTotale(request.getQuantiteInitiale());
		return TypeVaccinDto.from(typeVaccinRepository.save(t));
	}

	@Override
	@Transactional
	public TypeVaccinDto modifier(Long id, TypeVaccinRequest request) {
		TypeVaccin t = typeVaccinRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Type de vaccin introuvable"));
		t.setNom(request.getNom());
		t.setQuantiteTotale(request.getQuantiteInitiale());
		return TypeVaccinDto.from(typeVaccinRepository.save(t));
	}

	@Override
	@Transactional
	public void supprimer(Long id) {
		if (!typeVaccinRepository.existsById(id)) {
			throw new BusinessException("Type de vaccin introuvable");
		}
		if (vaccinationRepository.countByTypeVaccinId(id) > 0) {
			throw new BusinessException("Impossible de supprimer : des vaccinations utilisent ce type.");
		}
		typeVaccinRepository.deleteById(id);
	}
}
