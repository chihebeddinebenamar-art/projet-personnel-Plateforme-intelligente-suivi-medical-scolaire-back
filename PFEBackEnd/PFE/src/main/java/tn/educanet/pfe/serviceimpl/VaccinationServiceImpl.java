package tn.educanet.pfe.serviceimpl;

import java.time.LocalDate;
import java.util.List;

import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;

import com.tn.educanet.pfe.api.vaccinations.schema.VaccinationDto;
import com.tn.educanet.pfe.api.vaccinations.schema.VaccinationRequest;

import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.specification.VaccinationSpecification;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.persistence.TypeVaccin;
import tn.educanet.pfe.persistence.Vaccination;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.repository.TypeVaccinRepository;
import tn.educanet.pfe.repository.VaccinationRepository;
import tn.educanet.pfe.service.ParentNotificationService;
import tn.educanet.pfe.service.VaccinationService;
import tn.educanet.pfe.util.SchemaDateUtils;

@Service
public class VaccinationServiceImpl implements VaccinationService {

	@Resource
	private VaccinationRepository vaccinationRepository;

	@Resource
	private EleveRepository eleveRepository;

	@Resource
	private TypeVaccinRepository typeVaccinRepository;
	@Resource
	private Mapper mapper;
	@Resource
	private ParentNotificationService parentNotificationService;

	@Override
	@Transactional(readOnly = true)
	public List<VaccinationDto> listerParEleve(Long eleveId) {
		return vaccinationRepository.findByEleveIdOrderByDateVaccinationDesc(eleveId).stream()
				.map(this::mapVaccinationDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<VaccinationDto> listerFiltres(Long niveauId, Long classeId, Long typeVaccinId, String q,
			String numeroLot) {
		return vaccinationRepository
				.findAll(VaccinationSpecification.filtres(niveauId, classeId, typeVaccinId, q, numeroLot)).stream()
				.map(this::mapVaccinationDto).toList();
	}

	@Override
	@Transactional
	public VaccinationDto creer(VaccinationRequest request) {
		int dose = 1;
		Eleve eleve = eleveRepository.findById(request.getEleveId())
				.orElseThrow(() -> new BusinessException("Élève introuvable"));
		TypeVaccin type = typeVaccinRepository.findById(request.getTypeVaccinId())
				.orElseThrow(() -> new BusinessException("Type de vaccin introuvable"));
		var dateVaccination = SchemaDateUtils.toLocalDate(request.getDateVaccination());
		var datePrevue = SchemaDateUtils.toLocalDate(request.getDatePrevue());
		String persistedStatus = resolvePersistenceStatus(request.getStatus(), dateVaccination);
		if ("VACCINE".equals(persistedStatus) && dateVaccination == null) {
			throw new BusinessException("La date de vaccination est obligatoire lorsque le statut est « Vacciné ».");
		}
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
		v.setDateVaccination(dateVaccination);
		v.setDatePrevue(datePrevue);
		v.setNumeroLot(request.getNumeroLot());
		v.setStatus(persistedStatus);
		Vaccination saved = vaccinationRepository.save(v);
		if ("EN_ATTENTE".equals(saved.getStatus())) {
			parentNotificationService.publishVaccinationEnAttente(eleve, type.getNom(), datePrevue);
		} else {
			parentNotificationService.publishVaccination(eleve, type.getNom());
		}
		return mapVaccinationDto(vaccinationRepository.findDetailById(saved.getId()).orElse(saved));
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
		LocalDate newDateVaccination = SchemaDateUtils.toLocalDate(request.getDateVaccination());
		String newStatus = resolvePersistenceStatus(request.getStatus(), newDateVaccination);
		if ("VACCINE".equals(newStatus) && newDateVaccination == null) {
			throw new BusinessException("La date de vaccination est obligatoire lorsque le statut est « Vacciné ».");
		}
		v.setDateVaccination(newDateVaccination);
		v.setDatePrevue(SchemaDateUtils.toLocalDate(request.getDatePrevue()));
		v.setNumeroLot(request.getNumeroLot());
		v.setStatus(newStatus);
		vaccinationRepository.save(v);
		return mapVaccinationDto(vaccinationRepository.findDetailById(v.getId()).orElse(v));
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

	private VaccinationDto mapVaccinationDto(Vaccination vaccination) {
		VaccinationDto dto = mapper.map(vaccination, VaccinationDto.class);
		dto.setEleveNomComplet(nomComplet(vaccination.getEleve()));
		if (!StringUtils.hasText(dto.getStatus())) {
			dto.setStatus(deriveStatusFromDates(vaccination.getDateVaccination()));
		}
		return dto;
	}

	private String resolvePersistenceStatus(String rawStatus, LocalDate dateVaccination) {
		String normalized = normalizeVaccinationStatus(rawStatus);
		if (normalized != null) {
			return normalized;
		}
		return deriveStatusFromDates(dateVaccination);
	}

	private String normalizeVaccinationStatus(String raw) {
		if (!StringUtils.hasText(raw)) {
			return null;
		}
		String u = raw.trim().toUpperCase();
		if ("VACCINE".equals(u) || "EN_ATTENTE".equals(u) || "MANQUANT".equals(u)) {
			return u;
		}
		return "VACCINE";
	}

	private static String deriveStatusFromDates(LocalDate dateVaccination) {
		if (dateVaccination == null) {
			return "EN_ATTENTE";
		}
		LocalDate today = LocalDate.now();
		if (dateVaccination.isAfter(today)) {
			return "EN_ATTENTE";
		}
		return "VACCINE";
	}

	private String nomComplet(Eleve e) {
		if (e == null) {
			return "";
		}
		String nom = e.getNom() != null ? e.getNom() : "";
		String prenom = e.getPrenom() != null ? e.getPrenom() : "";
		return (nom + " " + prenom).trim();
	}
}
