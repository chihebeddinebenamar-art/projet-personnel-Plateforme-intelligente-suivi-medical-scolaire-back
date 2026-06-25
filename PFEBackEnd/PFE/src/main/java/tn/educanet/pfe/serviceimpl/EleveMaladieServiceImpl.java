package tn.educanet.pfe.serviceimpl;

import java.util.List;
import java.util.Locale;

import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tn.educanet.pfe.api.eleves.maladies.schema.MaladieEleveDto;
import com.tn.educanet.pfe.api.eleves.maladies.schema.MaladieEleveRequest;
import com.tn.educanet.pfe.api.maladies.schema.MaladieEleveListDto;

import jakarta.annotation.Resource;
import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.persistence.EleveMaladie;
import tn.educanet.pfe.repository.EleveMaladieRepository;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.service.EleveMaladieService;
import tn.educanet.pfe.service.ParentNotificationService;
import tn.educanet.pfe.specification.EleveMaladieSpecification;

@Service
public class EleveMaladieServiceImpl implements EleveMaladieService {

	@Resource
	private EleveRepository eleveRepository;

	@Resource
	private EleveMaladieRepository eleveMaladieRepository;
	@Resource
	private Mapper mapper;
	@Resource
	private ParentNotificationService parentNotificationService;

	@Override
	@Transactional(readOnly = true)
	public List<MaladieEleveListDto> listerFiltres(Long niveauId, Long classeId, String q) {
		return eleveMaladieRepository.findAll(EleveMaladieSpecification.filtres(niveauId, classeId, q)).stream()
				.map(this::mapMaladieListDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<MaladieEleveDto> lister(Long eleveId) {
		verifierEleve(eleveId);
		return eleveMaladieRepository.findByEleveIdOrderByTypeAscLibelleAsc(eleveId).stream()
				.map(m -> mapper.map(m, MaladieEleveDto.class))
				.toList();
	}

	@Override
	@Transactional
	public MaladieEleveDto creer(Long eleveId, MaladieEleveRequest request) {
		Eleve e = eleveRepository.findById(eleveId).orElseThrow(() -> new BusinessException("Élève introuvable"));
		tn.educanet.pfe.persistence.TypeMaladieEleve type = request.getType() == null ? null
				: tn.educanet.pfe.persistence.TypeMaladieEleve.valueOf(request.getType().name());
		String libelle = request.getLibelle() == null ? null : request.getLibelle().trim();
		String details = trimToNull(request.getDetails());
		if (isDuplicate(eleveId, type, libelle, details, null)) {
			throw new BusinessException("Entrée déjà existante pour cet élève.");
		}
		EleveMaladie m = new EleveMaladie();
		m.setEleve(e);
		m.setType(type);
		m.setLibelle(libelle);
		m.setDetails(details);
		EleveMaladie saved = eleveMaladieRepository.save(m);
		parentNotificationService.publishMaladie(e, saved.getType(), saved.getLibelle());
		return mapper.map(saved, MaladieEleveDto.class);
	}

	@Override
	@Transactional
	public MaladieEleveDto modifier(Long eleveId, Long id, MaladieEleveRequest request) {
		EleveMaladie m = eleveMaladieRepository.findByIdAndEleveId(id, eleveId)
				.orElseThrow(() -> new BusinessException("Entrée introuvable"));
		tn.educanet.pfe.persistence.TypeMaladieEleve type = request.getType() == null ? null
				: tn.educanet.pfe.persistence.TypeMaladieEleve.valueOf(request.getType().name());
		String libelle = request.getLibelle() == null ? null : request.getLibelle().trim();
		String details = trimToNull(request.getDetails());
		if (isDuplicate(eleveId, type, libelle, details, id)) {
			throw new BusinessException("Entrée déjà existante pour cet élève.");
		}
		m.setType(type);
		m.setLibelle(libelle);
		m.setDetails(details);
		return mapper.map(eleveMaladieRepository.save(m), MaladieEleveDto.class);
	}

	@Override
	@Transactional
	public void supprimer(Long eleveId, Long id) {
		EleveMaladie m = eleveMaladieRepository.findByIdAndEleveId(id, eleveId)
				.orElseThrow(() -> new BusinessException("Entrée introuvable"));
		eleveMaladieRepository.delete(m);
	}

	private void verifierEleve(Long eleveId) {
		if (!eleveRepository.existsById(eleveId)) {
			throw new BusinessException("Élève introuvable");
		}
	}

	private static String trimToNull(String s) {
		if (s == null) {
			return null;
		}
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}

	private boolean isDuplicate(Long eleveId, tn.educanet.pfe.persistence.TypeMaladieEleve type, String libelle,
			String details, Long excludeId) {
		String normalizedLibelle = normalize(libelle);
		String normalizedDetails = normalize(details);
		return eleveMaladieRepository.findByEleveIdOrderByTypeAscLibelleAsc(eleveId).stream()
				.filter(existing -> excludeId == null || !excludeId.equals(existing.getId()))
				.anyMatch(existing -> existing.getType() == type
						&& normalize(existing.getLibelle()).equals(normalizedLibelle)
						&& normalize(existing.getDetails()).equals(normalizedDetails));
	}

	private static String normalize(String value) {
		return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
	}

	private MaladieEleveListDto mapMaladieListDto(EleveMaladie maladie) {
		MaladieEleveListDto dto = mapper.map(maladie, MaladieEleveListDto.class);
		dto.setEleveNomComplet(nomComplet(maladie.getEleve()));
		return dto;
	}

	private String nomComplet(Eleve eleve) {
		if (eleve == null) {
			return "";
		}
		String nom = eleve.getNom() != null ? eleve.getNom() : "";
		String prenom = eleve.getPrenom() != null ? eleve.getPrenom() : "";
		return (nom + " " + prenom).trim();
	}
}
