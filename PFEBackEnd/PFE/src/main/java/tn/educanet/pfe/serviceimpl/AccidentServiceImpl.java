package tn.educanet.pfe.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.educanet.pfe.api.dto.AccidentDto;
import tn.educanet.pfe.api.dto.AccidentRequest;
import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Accident;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.repository.AccidentRepository;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.service.AccidentService;
import tn.educanet.pfe.specification.AccidentSpecification;

@Service
public class AccidentServiceImpl implements AccidentService {

	private final AccidentRepository accidentRepository;
	private final EleveRepository eleveRepository;

	public AccidentServiceImpl(AccidentRepository accidentRepository, EleveRepository eleveRepository) {
		this.accidentRepository = accidentRepository;
		this.eleveRepository = eleveRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccidentDto> listerParEleve(Long eleveId) {
		return accidentRepository.findByEleveIdOrderByDateAccidentDesc(eleveId).stream().map(AccidentDto::from)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccidentDto> listerFiltres(Long niveauId, Long classeId, String q) {
		return accidentRepository.findAll(AccidentSpecification.filtres(niveauId, classeId, q)).stream()
				.map(AccidentDto::from).toList();
	}

	@Override
	@Transactional
	public AccidentDto creer(AccidentRequest request) {
		Eleve eleve = eleveRepository.findById(request.getEleveId())
				.orElseThrow(() -> new BusinessException("Élève introuvable"));
		Accident a = new Accident();
		a.setEleve(eleve);
		a.setDateAccident(request.getDateAccident());
		a.setDescription(request.getDescription().trim());
		a.setDiagnostic(request.getDiagnostic().trim());
		a.setTraitement(request.getTraitement().trim());
		a.setEtat(request.getEtat().trim());
		Accident saved = accidentRepository.save(a);
		return AccidentDto.from(accidentRepository.findDetailById(saved.getId()).orElse(saved));
	}

	@Override
	@Transactional
	public AccidentDto modifier(Long id, AccidentRequest request) {
		Accident a = accidentRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Émergence scolaire introuvable"));
		Eleve eleve = eleveRepository.findById(request.getEleveId())
				.orElseThrow(() -> new BusinessException("Élève introuvable"));
		a.setEleve(eleve);
		a.setDateAccident(request.getDateAccident());
		a.setDescription(request.getDescription().trim());
		a.setDiagnostic(request.getDiagnostic().trim());
		a.setTraitement(request.getTraitement().trim());
		a.setEtat(request.getEtat().trim());
		accidentRepository.save(a);
		return AccidentDto.from(accidentRepository.findDetailById(a.getId()).orElse(a));
	}

	@Override
	@Transactional
	public void supprimer(Long id) {
		if (!accidentRepository.existsById(id)) {
			throw new BusinessException("Émergence scolaire introuvable");
		}
		accidentRepository.deleteById(id);
	}
}
