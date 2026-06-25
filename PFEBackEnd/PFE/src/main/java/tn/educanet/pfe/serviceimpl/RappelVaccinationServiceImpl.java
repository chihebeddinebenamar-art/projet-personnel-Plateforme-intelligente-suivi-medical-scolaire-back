package tn.educanet.pfe.serviceimpl;

import java.util.HashSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tn.educanet.pfe.api.rappels.vaccination.schema.RappelVaccinationDto;
import com.tn.educanet.pfe.api.rappels.vaccination.schema.RappelVaccinationClasseDto;
import com.tn.educanet.pfe.api.rappels.vaccination.schema.RappelVaccinationRequest;

import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Classe;
import tn.educanet.pfe.persistence.RappelVaccination;
import tn.educanet.pfe.persistence.TypeVaccin;
import tn.educanet.pfe.repository.ClasseRepository;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.repository.RappelVaccinationRepository;
import tn.educanet.pfe.repository.TypeVaccinRepository;
import tn.educanet.pfe.service.ParentNotificationService;
import tn.educanet.pfe.service.RappelVaccinationService;
import tn.educanet.pfe.util.SchemaDateUtils;

@Service
public class RappelVaccinationServiceImpl implements RappelVaccinationService {

	private final RappelVaccinationRepository rappelVaccinationRepository;
	private final TypeVaccinRepository typeVaccinRepository;
	private final ClasseRepository classeRepository;
	private final EleveRepository eleveRepository;
	private final ParentNotificationService parentNotificationService;
	private final Mapper mapper;

	public RappelVaccinationServiceImpl(RappelVaccinationRepository rappelVaccinationRepository,
			TypeVaccinRepository typeVaccinRepository, ClasseRepository classeRepository,
			EleveRepository eleveRepository, ParentNotificationService parentNotificationService, Mapper mapper) {
		this.rappelVaccinationRepository = rappelVaccinationRepository;
		this.typeVaccinRepository = typeVaccinRepository;
		this.classeRepository = classeRepository;
		this.eleveRepository = eleveRepository;
		this.parentNotificationService = parentNotificationService;
		this.mapper = mapper;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RappelVaccinationDto> lister() {
		return rappelVaccinationRepository.findAllByOrderByDatePrevueDescIdDesc().stream()
				.map(this::mapRappelDto)
				.toList();
	}

	@Override
	@Transactional
	public RappelVaccinationDto creer(RappelVaccinationRequest request) {
		TypeVaccin type = typeVaccinRepository.findById(request.getTypeVaccinId())
				.orElseThrow(() -> new BusinessException("Type de vaccin introuvable"));
		Set<Classe> classes = loadClasses(classeIds(request));
		RappelVaccination r = new RappelVaccination();
		r.setDatePrevue(SchemaDateUtils.toLocalDate(request.getDatePrevue()));
		r.setTypeVaccin(type);
		r.setRemarque(trimToNull(request.getRemarque()));
		r.setClasses(classes);
		RappelVaccination saved = rappelVaccinationRepository.save(r);
		parentNotificationService.publishVaccinationReminder(
				classes.stream().flatMap(c -> eleveRepository.findByClasseIdOrderByNomAscPrenomAsc(c.getId()).stream()).toList(),
				type.getNom(), saved.getDatePrevue(), saved.getRemarque());
		return mapRappelDto(saved);
	}

	@Override
	@Transactional
	public RappelVaccinationDto modifier(Long id, RappelVaccinationRequest request) {
		RappelVaccination r = rappelVaccinationRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Rappel introuvable"));
		TypeVaccin type = typeVaccinRepository.findById(request.getTypeVaccinId())
				.orElseThrow(() -> new BusinessException("Type de vaccin introuvable"));
		r.setDatePrevue(SchemaDateUtils.toLocalDate(request.getDatePrevue()));
		r.setTypeVaccin(type);
		r.setRemarque(trimToNull(request.getRemarque()));
		r.getClasses().clear();
		r.getClasses().addAll(loadClasses(classeIds(request)));
		return mapRappelDto(rappelVaccinationRepository.save(r));
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

	private List<Long> classeIds(RappelVaccinationRequest request) {
		if (request == null || request.getClasseIds() == null || request.getClasseIds().getClasseId() == null) {
			return List.of();
		}
		return request.getClasseIds().getClasseId();
	}

	private RappelVaccinationDto mapRappelDto(RappelVaccination rappel) {
		RappelVaccinationDto dto = mapper.map(rappel, RappelVaccinationDto.class);
		if (rappel.getClasses() != null && !rappel.getClasses().isEmpty()) {
			List<Classe> sorted = rappel.getClasses().stream()
					.sorted(Comparator.comparing(Classe::getNom, Comparator.nullsLast(String::compareToIgnoreCase)))
					.toList();
			RappelVaccinationDto.Classes holder = new RappelVaccinationDto.Classes();
			for (Classe classe : sorted) {
				RappelVaccinationClasseDto classDto = new RappelVaccinationClasseDto();
				classDto.setId(classe.getId());
				classDto.setNom(classe.getNom());
				if (classe.getNiveau() != null) {
					classDto.setNiveauId(classe.getNiveau().getId());
					classDto.setNiveauNom(classe.getNiveau().getNom());
				}
				holder.getItem().add(classDto);
			}
			dto.setClasses(holder);
		}
		return dto;
	}
}
