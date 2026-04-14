package tn.educanet.pfe.serviceimpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.educanet.pfe.api.dto.RappelVaccinationDto;
import tn.educanet.pfe.api.dto.RappelVaccinationRequest;
import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Classe;
import tn.educanet.pfe.persistence.RappelVaccination;
import tn.educanet.pfe.persistence.TypeVaccin;
import tn.educanet.pfe.repository.ClasseRepository;
import tn.educanet.pfe.repository.RappelVaccinationRepository;
import tn.educanet.pfe.repository.TypeVaccinRepository;
import tn.educanet.pfe.service.RappelVaccinationService;

@Service
public class RappelVaccinationServiceImpl implements RappelVaccinationService {

	private final RappelVaccinationRepository rappelVaccinationRepository;
	private final TypeVaccinRepository typeVaccinRepository;
	private final ClasseRepository classeRepository;

	public RappelVaccinationServiceImpl(RappelVaccinationRepository rappelVaccinationRepository,
			TypeVaccinRepository typeVaccinRepository, ClasseRepository classeRepository) {
		this.rappelVaccinationRepository = rappelVaccinationRepository;
		this.typeVaccinRepository = typeVaccinRepository;
		this.classeRepository = classeRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RappelVaccinationDto> lister() {
		return rappelVaccinationRepository.findAllByOrderByDatePrevueDescIdDesc().stream().map(RappelVaccinationDto::from)
				.toList();
	}

	@Override
	@Transactional
	public RappelVaccinationDto creer(RappelVaccinationRequest request) {
		TypeVaccin type = typeVaccinRepository.findById(request.getTypeVaccinId())
				.orElseThrow(() -> new BusinessException("Type de vaccin introuvable"));
		Set<Classe> classes = loadClasses(request.getClasseIds());
		RappelVaccination r = new RappelVaccination();
		r.setDatePrevue(request.getDatePrevue());
		r.setTypeVaccin(type);
		r.setRemarque(trimToNull(request.getRemarque()));
		r.setClasses(classes);
		return RappelVaccinationDto.from(rappelVaccinationRepository.save(r));
	}

	@Override
	@Transactional
	public RappelVaccinationDto modifier(Long id, RappelVaccinationRequest request) {
		RappelVaccination r = rappelVaccinationRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Rappel introuvable"));
		TypeVaccin type = typeVaccinRepository.findById(request.getTypeVaccinId())
				.orElseThrow(() -> new BusinessException("Type de vaccin introuvable"));
		r.setDatePrevue(request.getDatePrevue());
		r.setTypeVaccin(type);
		r.setRemarque(trimToNull(request.getRemarque()));
		r.getClasses().clear();
		r.getClasses().addAll(loadClasses(request.getClasseIds()));
		return RappelVaccinationDto.from(rappelVaccinationRepository.save(r));
	}

	@Override
	@Transactional
	public void supprimer(Long id) {
		if (!rappelVaccinationRepository.existsById(id)) {
			throw new BusinessException("Rappel introuvable");
		}
		rappelVaccinationRepository.deleteById(id);
	}

	private Set<Classe> loadClasses(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			throw new BusinessException("Sélectionnez au moins une classe.");
		}
		List<Classe> found = classeRepository.findAllById(ids);
		if (found.size() != ids.size()) {
			throw new BusinessException("Une ou plusieurs classes sont introuvables.");
		}
		Long niveauId = null;
		for (Classe c : found) {
			if (c.getNiveau() == null) {
				throw new BusinessException("Classe sans niveau : " + c.getNom());
			}
			if (niveauId == null) {
				niveauId = c.getNiveau().getId();
			} else if (!niveauId.equals(c.getNiveau().getId())) {
				throw new BusinessException("Toutes les classes d’un rappel doivent être du même niveau.");
			}
		}
		return new HashSet<>(found);
	}

	private static String trimToNull(String s) {
		if (s == null) {
			return null;
		}
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}
}
