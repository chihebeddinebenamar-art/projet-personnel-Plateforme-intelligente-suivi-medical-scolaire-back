package tn.educanet.pfe.serviceimpl;

import java.util.List;

import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tn.educanet.pfe.api.accidents.schema.AccidentDto;
import com.tn.educanet.pfe.api.accidents.schema.AccidentRequest;

import jakarta.annotation.Resource;
import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Accident;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.repository.AccidentRepository;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.service.AccidentService;
import tn.educanet.pfe.service.ParentNotificationService;
import tn.educanet.pfe.specification.AccidentSpecification;
import tn.educanet.pfe.util.SchemaDateUtils;

@Service
public class AccidentServiceImpl implements AccidentService {

	@Resource
	private AccidentRepository accidentRepository;

	@Resource
	private EleveRepository eleveRepository;
	@Resource
	private Mapper mapper;
	@Resource
	private ParentNotificationService parentNotificationService;

	@Override
	@Transactional(readOnly = true)
	public List<AccidentDto> listerParEleve(Long eleveId) {
		return accidentRepository.findByEleveIdOrderByDateAccidentDesc(eleveId).stream().map(this::mapAccidentDto)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccidentDto> listerFiltres(Long niveauId, Long classeId, String q) {
		return accidentRepository.findAll(AccidentSpecification.filtres(niveauId, classeId, q)).stream()
				.map(this::mapAccidentDto).toList();
	}

	@Override
	@Transactional
	public AccidentDto creer(AccidentRequest request) {
		Eleve eleve = eleveRepository.findById(request.getEleveId())
				.orElseThrow(() -> new BusinessException("Élève introuvable"));
		Accident a = new Accident();
		a.setEleve(eleve);
		a.setDateAccident(SchemaDateUtils.toLocalDate(request.getDateAccident()));
		a.setDescription(request.getDescription().trim());
		a.setDiagnostic(request.getDiagnostic().trim());
		a.setTraitement(request.getTraitement().trim());
		a.setEtat(request.getEtat().trim());
		Accident saved = accidentRepository.save(a);
		parentNotificationService.publishAccident(eleve, a.getDiagnostic());
		return mapAccidentDto(accidentRepository.findDetailById(saved.getId()).orElse(saved));
	}

	@Override
	@Transactional
	public AccidentDto modifier(Long id, AccidentRequest request) {
		Accident a = accidentRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Émergence scolaire introuvable"));
		Eleve eleve = eleveRepository.findById(request.getEleveId())
				.orElseThrow(() -> new BusinessException("Élève introuvable"));
		a.setEleve(eleve);
		a.setDateAccident(SchemaDateUtils.toLocalDate(request.getDateAccident()));
		a.setDescription(request.getDescription().trim());
		a.setDiagnostic(request.getDiagnostic().trim());
		a.setTraitement(request.getTraitement().trim());
		a.setEtat(request.getEtat().trim());
		accidentRepository.save(a);
		return mapAccidentDto(accidentRepository.findDetailById(a.getId()).orElse(a));
	}

	@Override
	@Transactional
	public void supprimer(Long id) {
		if (!accidentRepository.existsById(id)) {
			throw new BusinessException("Émergence scolaire introuvable");
		}
		accidentRepository.deleteById(id);
	}

	private AccidentDto mapAccidentDto(Accident accident) {
		AccidentDto dto = mapper.map(accident, AccidentDto.class);
		dto.setEleveNomComplet(nomComplet(accident.getEleve()));
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
