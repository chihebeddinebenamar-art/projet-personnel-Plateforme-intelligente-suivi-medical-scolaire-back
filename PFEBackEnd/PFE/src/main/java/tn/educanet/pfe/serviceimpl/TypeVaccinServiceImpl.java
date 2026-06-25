package tn.educanet.pfe.serviceimpl;

import java.util.List;

import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import com.tn.educanet.pfe.api.vaccins.types.schema.TypeVaccinDto;
import com.tn.educanet.pfe.api.vaccins.types.schema.TypeVaccinRequest;

import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.TypeVaccin;
import tn.educanet.pfe.repository.VaccinationRepository;
import tn.educanet.pfe.repository.TypeVaccinRepository;
import tn.educanet.pfe.service.TypeVaccinService;

@Service
public class TypeVaccinServiceImpl implements TypeVaccinService {

	@Resource
	private TypeVaccinRepository typeVaccinRepository;

	@Resource
	private VaccinationRepository vaccinationRepository;
	@Resource
	private Mapper mapper;

	@Override
	@Transactional(readOnly = true)
	public List<TypeVaccinDto> lister() {
		return typeVaccinRepository.findAll().stream().map(t -> mapper.map(t, TypeVaccinDto.class)).toList();
	}

	@Override
	@Transactional
	public TypeVaccinDto creer(TypeVaccinRequest request) {
		TypeVaccin t = new TypeVaccin();
		t.setNom(request.getNom());
		t.setQuantiteTotale(request.getQuantiteInitiale());
		return mapper.map(typeVaccinRepository.save(t), TypeVaccinDto.class);
	}

	@Override
	@Transactional
	public TypeVaccinDto modifier(Long id, TypeVaccinRequest request) {
		TypeVaccin t = typeVaccinRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Type de vaccin introuvable"));
		t.setNom(request.getNom());
		t.setQuantiteTotale(request.getQuantiteInitiale());
		return mapper.map(typeVaccinRepository.save(t), TypeVaccinDto.class);
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
