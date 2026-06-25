package tn.educanet.pfe.serviceimpl;

import java.util.List;

import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tn.educanet.pfe.api.consultations.schema.ConsultationDto;
import com.tn.educanet.pfe.api.consultations.schema.ConsultationRequest;

import jakarta.annotation.Resource;
import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Consultation;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.repository.ConsultationRepository;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.service.ConsultationService;
import tn.educanet.pfe.service.ParentNotificationService;
import tn.educanet.pfe.specification.ConsultationSpecification;
import tn.educanet.pfe.util.SchemaDateUtils;

@Service
public class ConsultationServiceImpl implements ConsultationService {

	@Resource
	private ConsultationRepository consultationRepository;

	@Resource
	private EleveRepository eleveRepository;
	@Resource
	private Mapper mapper;
	@Resource
	private ParentNotificationService parentNotificationService;

	@Override
	@Transactional(readOnly = true)
	public List<ConsultationDto> listerParEleve(Long eleveId) {
		return consultationRepository.findByEleveIdOrderByDateConsultationDesc(eleveId).stream()
				.map(this::mapConsultationDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConsultationDto> listerFiltres(Long niveauId, Long classeId, String q) {
		return consultationRepository.findAll(ConsultationSpecification.filtres(niveauId, classeId, q)).stream()
				.map(this::mapConsultationDto).toList();
	}

	@Override
	@Transactional
	public ConsultationDto creer(ConsultationRequest request) {
		Eleve eleve = eleveRepository.findById(request.getEleveId())
				.orElseThrow(() -> new BusinessException("Élève introuvable"));
		Consultation c = new Consultation();
		c.setEleve(eleve);
		c.setDateConsultation(SchemaDateUtils.toLocalDate(request.getDateConsultation()));
		c.setTemperature(request.getTemperature());
		c.setVomissement(Boolean.TRUE.equals(request.isVomissement()));
		c.setDiarrhee(Boolean.TRUE.equals(request.isDiarrhee()));
		c.setRapport(request.getRapport());
		c.setProchaineConsultation(SchemaDateUtils.toLocalDate(request.getProchaineConsultation()));
		c.setTraitement(request.getTraitement().trim());
		Consultation saved = consultationRepository.save(c);
		parentNotificationService.publishConsultation(eleve);
		return mapConsultationDto(consultationRepository.findDetailById(saved.getId()).orElse(saved));
	}

	@Override
	@Transactional
	public ConsultationDto modifier(Long id, ConsultationRequest request) {
		Consultation c = consultationRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Consultation introuvable"));
		Eleve eleve = eleveRepository.findById(request.getEleveId())
				.orElseThrow(() -> new BusinessException("Élève introuvable"));
		c.setEleve(eleve);
		c.setDateConsultation(SchemaDateUtils.toLocalDate(request.getDateConsultation()));
		c.setTemperature(request.getTemperature());
		c.setVomissement(Boolean.TRUE.equals(request.isVomissement()));
		c.setDiarrhee(Boolean.TRUE.equals(request.isDiarrhee()));
		c.setRapport(request.getRapport());
		c.setProchaineConsultation(SchemaDateUtils.toLocalDate(request.getProchaineConsultation()));
		c.setTraitement(request.getTraitement().trim());
		consultationRepository.save(c);
		return mapConsultationDto(consultationRepository.findDetailById(c.getId()).orElse(c));
	}

	@Override
	@Transactional
	public void supprimer(Long id) {
		if (!consultationRepository.existsById(id)) {
			throw new BusinessException("Consultation introuvable");
		}
		consultationRepository.deleteById(id);
	}

	private ConsultationDto mapConsultationDto(Consultation consultation) {
		ConsultationDto dto = mapper.map(consultation, ConsultationDto.class);
		dto.setEleveNomComplet(nomComplet(consultation.getEleve()));
		return dto;
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
