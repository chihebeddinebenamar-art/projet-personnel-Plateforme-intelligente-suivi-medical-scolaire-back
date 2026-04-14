package tn.educanet.pfe.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.educanet.pfe.api.dto.ConsultationDto;
import tn.educanet.pfe.api.dto.ConsultationRequest;
import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Consultation;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.repository.ConsultationRepository;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.service.ConsultationService;
import tn.educanet.pfe.specification.ConsultationSpecification;

@Service
public class ConsultationServiceImpl implements ConsultationService {

	private final ConsultationRepository consultationRepository;
	private final EleveRepository eleveRepository;

	public ConsultationServiceImpl(ConsultationRepository consultationRepository, EleveRepository eleveRepository) {
		this.consultationRepository = consultationRepository;
		this.eleveRepository = eleveRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConsultationDto> listerParEleve(Long eleveId) {
		return consultationRepository.findByEleveIdOrderByDateConsultationDesc(eleveId).stream()
				.map(ConsultationDto::from).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConsultationDto> listerFiltres(Long niveauId, Long classeId, String q) {
		return consultationRepository.findAll(ConsultationSpecification.filtres(niveauId, classeId, q)).stream()
				.map(ConsultationDto::from).toList();
	}

	@Override
	@Transactional
	public ConsultationDto creer(ConsultationRequest request) {
		Eleve eleve = eleveRepository.findById(request.getEleveId())
				.orElseThrow(() -> new BusinessException("Élève introuvable"));
		Consultation c = new Consultation();
		c.setEleve(eleve);
		c.setDateConsultation(request.getDateConsultation());
		c.setTemperature(request.getTemperature());
		c.setVomissement(Boolean.TRUE.equals(request.getVomissement()));
		c.setDiarrhee(Boolean.TRUE.equals(request.getDiarrhee()));
		c.setRapport(request.getRapport());
		c.setProchaineConsultation(request.getProchaineConsultation());
		c.setTraitement(request.getTraitement().trim());
		Consultation saved = consultationRepository.save(c);
		return ConsultationDto.from(consultationRepository.findDetailById(saved.getId()).orElse(saved));
	}

	@Override
	@Transactional
	public ConsultationDto modifier(Long id, ConsultationRequest request) {
		Consultation c = consultationRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Consultation introuvable"));
		Eleve eleve = eleveRepository.findById(request.getEleveId())
				.orElseThrow(() -> new BusinessException("Élève introuvable"));
		c.setEleve(eleve);
		c.setDateConsultation(request.getDateConsultation());
		c.setTemperature(request.getTemperature());
		c.setVomissement(Boolean.TRUE.equals(request.getVomissement()));
		c.setDiarrhee(Boolean.TRUE.equals(request.getDiarrhee()));
		c.setRapport(request.getRapport());
		c.setProchaineConsultation(request.getProchaineConsultation());
		c.setTraitement(request.getTraitement().trim());
		consultationRepository.save(c);
		return ConsultationDto.from(consultationRepository.findDetailById(c.getId()).orElse(c));
	}

	@Override
	@Transactional
	public void supprimer(Long id) {
		if (!consultationRepository.existsById(id)) {
			throw new BusinessException("Consultation introuvable");
		}
		consultationRepository.deleteById(id);
	}
}
