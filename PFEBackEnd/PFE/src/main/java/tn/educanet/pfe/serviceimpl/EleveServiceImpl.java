package tn.educanet.pfe.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.educanet.pfe.api.dto.AccidentDto;
import tn.educanet.pfe.api.dto.ConsultationDto;
import tn.educanet.pfe.api.dto.EleveDto;
import tn.educanet.pfe.api.dto.EleveRequest;
import tn.educanet.pfe.api.dto.FicheMedicaleDto;
import tn.educanet.pfe.api.dto.MaladieEleveDto;
import tn.educanet.pfe.api.dto.VaccinationDto;
import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Classe;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.repository.AccidentRepository;
import tn.educanet.pfe.repository.ClasseRepository;
import tn.educanet.pfe.repository.ConsultationRepository;
import tn.educanet.pfe.repository.EleveCarnetNumeriqueRepository;
import tn.educanet.pfe.repository.EleveMaladieRepository;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.repository.VaccinationRepository;
import tn.educanet.pfe.service.EleveCarnetNumeriqueService;
import tn.educanet.pfe.service.EleveService;
import tn.educanet.pfe.specification.EleveSpecification;

@Service
public class EleveServiceImpl implements EleveService {

	private final EleveRepository eleveRepository;
	private final ClasseRepository classeRepository;
	private final VaccinationRepository vaccinationRepository;
	private final ConsultationRepository consultationRepository;
	private final AccidentRepository accidentRepository;
	private final EleveMaladieRepository eleveMaladieRepository;
	private final EleveCarnetNumeriqueRepository eleveCarnetNumeriqueRepository;
	private final EleveCarnetNumeriqueService eleveCarnetNumeriqueService;

	public EleveServiceImpl(EleveRepository eleveRepository, ClasseRepository classeRepository,
			VaccinationRepository vaccinationRepository, ConsultationRepository consultationRepository,
			AccidentRepository accidentRepository, EleveMaladieRepository eleveMaladieRepository,
			EleveCarnetNumeriqueRepository eleveCarnetNumeriqueRepository,
			EleveCarnetNumeriqueService eleveCarnetNumeriqueService) {
		this.eleveRepository = eleveRepository;
		this.classeRepository = classeRepository;
		this.vaccinationRepository = vaccinationRepository;
		this.consultationRepository = consultationRepository;
		this.accidentRepository = accidentRepository;
		this.eleveMaladieRepository = eleveMaladieRepository;
		this.eleveCarnetNumeriqueRepository = eleveCarnetNumeriqueRepository;
		this.eleveCarnetNumeriqueService = eleveCarnetNumeriqueService;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EleveDto> listerFiltres(Long niveauId, Long classeId, String recherche) {
		return eleveRepository.findAll(EleveSpecification.filtres(niveauId, classeId, recherche)).stream()
				.map(EleveDto::from).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<EleveDto> parClasse(Long classeId) {
		return eleveRepository.findByClasseIdOrderByNomAscPrenomAsc(classeId).stream().map(EleveDto::from).toList();
	}

	@Override
	@Transactional
	public EleveDto creer(EleveRequest request) {
		Classe classe = classeRepository.findById(request.getClasseId())
				.orElseThrow(() -> new BusinessException("Classe introuvable"));
		String mat = request.getMatricule() != null ? request.getMatricule().trim() : null;
		if (mat != null && !mat.isEmpty() && eleveRepository.existsByMatricule(mat)) {
			throw new BusinessException("Ce matricule existe déjà : " + mat);
		}
		Eleve e = new Eleve();
		e.setMatricule(mat);
		e.setNom(request.getNom());
		e.setPrenom(request.getPrenom());
		e.setDateNaissance(request.getDateNaissance());
		e.setClasse(classe);
		return EleveDto.from(eleveRepository.save(e));
	}

	@Override
	@Transactional
	public EleveDto modifier(Long id, EleveRequest request) {
		Eleve e = eleveRepository.findById(id).orElseThrow(() -> new BusinessException("Élève introuvable"));
		Classe classe = classeRepository.findById(request.getClasseId())
				.orElseThrow(() -> new BusinessException("Classe introuvable"));
		String mat = request.getMatricule() != null ? request.getMatricule().trim() : null;
		if (mat != null && !mat.isEmpty() && eleveRepository.existsByMatriculeAndIdNot(mat, id)) {
			throw new BusinessException("Ce matricule est déjà utilisé par un autre élève : " + mat);
		}
		e.setMatricule(mat);
		e.setNom(request.getNom());
		e.setPrenom(request.getPrenom());
		e.setDateNaissance(request.getDateNaissance());
		e.setClasse(classe);
		return EleveDto.from(eleveRepository.save(e));
	}

	@Override
	@Transactional
	public void supprimer(Long id) {
		if (!eleveRepository.existsById(id)) {
			throw new BusinessException("Élève introuvable");
		}
		eleveCarnetNumeriqueService.supprimerSiPresent(id);
		eleveRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public EleveDto get(Long id) {
		Eleve e = eleveRepository.findById(id).orElseThrow(() -> new BusinessException("Élève introuvable"));
		return EleveDto.from(e);
	}

	@Override
	@Transactional(readOnly = true)
	public FicheMedicaleDto ficheMedicale(Long eleveId) {
		Eleve e = eleveRepository.findById(eleveId).orElseThrow(() -> new BusinessException("Élève introuvable"));
		EleveDto ed = EleveDto.from(e);
		List<VaccinationDto> vac = vaccinationRepository.findByEleveIdOrderByDateVaccinationDesc(eleveId).stream()
				.map(VaccinationDto::from).toList();
		List<ConsultationDto> cons = consultationRepository.findByEleveIdOrderByDateConsultationDesc(eleveId).stream()
				.map(ConsultationDto::from).toList();
		List<AccidentDto> acc = accidentRepository.findByEleveIdOrderByDateAccidentDesc(eleveId).stream()
				.map(AccidentDto::from).toList();
		List<MaladieEleveDto> mal = eleveMaladieRepository.findByEleveIdOrderByTypeAscLibelleAsc(eleveId).stream()
				.map(MaladieEleveDto::from).toList();
		var carnetOpt = eleveCarnetNumeriqueRepository.findByEleveId(eleveId);
		boolean carnetPresent = carnetOpt.isPresent();
		Long carnetVer = carnetOpt.map(c -> c.getUpdatedAt().toEpochMilli()).orElse(null);
		return new FicheMedicaleDto(ed, vac, cons, acc, mal, carnetPresent, carnetVer);
	}
}
