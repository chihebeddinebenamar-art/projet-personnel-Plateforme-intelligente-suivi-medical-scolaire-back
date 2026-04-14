package tn.educanet.pfe.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.educanet.pfe.api.dto.VaccinationDto;
import tn.educanet.pfe.api.dto.VaccinationRequest;
import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.specification.VaccinationSpecification;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.persistence.TypeVaccin;
import tn.educanet.pfe.persistence.Vaccination;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.repository.TypeVaccinRepository;
import tn.educanet.pfe.repository.VaccinationRepository;
import tn.educanet.pfe.service.VaccinationService;

@Service
public class VaccinationServiceImpl implements VaccinationService {

	private final VaccinationRepository vaccinationRepository;
	private final EleveRepository eleveRepository;
	private final TypeVaccinRepository typeVaccinRepository;

	public VaccinationServiceImpl(VaccinationRepository vaccinationRepository, EleveRepository eleveRepository,
			TypeVaccinRepository typeVaccinRepository) {
		this.vaccinationRepository = vaccinationRepository;
		this.eleveRepository = eleveRepository;
		this.typeVaccinRepository = typeVaccinRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<VaccinationDto> listerParEleve(Long eleveId) {
		return vaccinationRepository.findByEleveIdOrderByDateVaccinationDesc(eleveId).stream()
				.map(VaccinationDto::from).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<VaccinationDto> listerFiltres(Long niveauId, Long classeId, Long typeVaccinId, String q,
			String numeroLot) {
		return vaccinationRepository
				.findAll(VaccinationSpecification.filtres(niveauId, classeId, typeVaccinId, q, numeroLot)).stream()
				.map(VaccinationDto::from).toList();
	}

	@Override
	@Transactional
	public VaccinationDto creer(VaccinationRequest request) {
		int dose = 1;
		Eleve eleve = eleveRepository.findById(request.getEleveId())
				.orElseThrow(() -> new BusinessException("Élève introuvable"));
		TypeVaccin type = typeVaccinRepository.findById(request.getTypeVaccinId())
				.orElseThrow(() -> new BusinessException("Type de vaccin introuvable"));
		if (vaccinationRepository.existsByEleve_IdAndTypeVaccin_Id(request.getEleveId(), request.getTypeVaccinId())) {
			throw new BusinessException(
					"Cet élève a déjà une vaccination enregistrée pour ce type de vaccin. Impossible d'en ajouter une autre.");
		}
		if (type.getQuantiteTotale() < dose) {
			throw new BusinessException("Stock insuffisant pour ce type de vaccin.");
		}
		type.setQuantiteTotale(type.getQuantiteTotale() - dose);
		typeVaccinRepository.save(type);

		Vaccination v = new Vaccination();
		v.setEleve(eleve);
		v.setTypeVaccin(type);
		v.setDose(dose);
		v.setDateVaccination(request.getDateVaccination());
		v.setDatePrevue(request.getDatePrevue());
		v.setNumeroLot(request.getNumeroLot());
		Vaccination saved = vaccinationRepository.save(v);
		return VaccinationDto.from(vaccinationRepository.findDetailById(saved.getId()).orElse(saved));
	}

	@Override
	@Transactional
	public VaccinationDto modifier(Long id, VaccinationRequest request) {
		Vaccination v = vaccinationRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Vaccination introuvable"));
		int oldDose = v.getDose();
		TypeVaccin oldType = v.getTypeVaccin();
		int newDose = 1;
		TypeVaccin newType = typeVaccinRepository.findById(request.getTypeVaccinId())
				.orElseThrow(() -> new BusinessException("Type de vaccin introuvable"));
		Eleve eleve = eleveRepository.findById(request.getEleveId())
				.orElseThrow(() -> new BusinessException("Élève introuvable"));

		if (oldType.getId().equals(newType.getId())) {
			int delta = newDose - oldDose;
			if (delta > 0) {
				if (oldType.getQuantiteTotale() < delta) {
					throw new BusinessException("Stock insuffisant pour ajuster la dose.");
				}
				oldType.setQuantiteTotale(oldType.getQuantiteTotale() - delta);
			} else if (delta < 0) {
				oldType.setQuantiteTotale(oldType.getQuantiteTotale() - delta);
			}
			typeVaccinRepository.save(oldType);
		} else {
			oldType.setQuantiteTotale(oldType.getQuantiteTotale() + oldDose);
			typeVaccinRepository.save(oldType);
			if (newType.getQuantiteTotale() < newDose) {
				oldType.setQuantiteTotale(oldType.getQuantiteTotale() - oldDose);
				typeVaccinRepository.save(oldType);
				throw new BusinessException("Stock insuffisant sur le nouveau type de vaccin.");
			}
			newType.setQuantiteTotale(newType.getQuantiteTotale() - newDose);
			typeVaccinRepository.save(newType);
		}

		v.setEleve(eleve);
		v.setTypeVaccin(newType);
		v.setDose(newDose);
		v.setDateVaccination(request.getDateVaccination());
		v.setDatePrevue(request.getDatePrevue());
		v.setNumeroLot(request.getNumeroLot());
		vaccinationRepository.save(v);
		return VaccinationDto.from(vaccinationRepository.findDetailById(v.getId()).orElse(v));
	}

	@Override
	@Transactional
	public void supprimer(Long id) {
		Vaccination v = vaccinationRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Vaccination introuvable"));
		TypeVaccin type = v.getTypeVaccin();
		type.setQuantiteTotale(type.getQuantiteTotale() + v.getDose());
		typeVaccinRepository.save(type);
		vaccinationRepository.delete(v);
	}
}
