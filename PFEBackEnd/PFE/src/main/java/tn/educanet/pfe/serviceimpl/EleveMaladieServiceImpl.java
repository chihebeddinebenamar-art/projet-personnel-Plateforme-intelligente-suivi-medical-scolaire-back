package tn.educanet.pfe.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.educanet.pfe.api.dto.MaladieEleveDto;
import tn.educanet.pfe.api.dto.MaladieEleveListDto;
import tn.educanet.pfe.api.dto.MaladieEleveRequest;
import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.persistence.EleveMaladie;
import tn.educanet.pfe.repository.EleveMaladieRepository;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.service.EleveMaladieService;
import tn.educanet.pfe.specification.EleveMaladieSpecification;

@Service
public class EleveMaladieServiceImpl implements EleveMaladieService {

	private final EleveRepository eleveRepository;
	private final EleveMaladieRepository eleveMaladieRepository;

	public EleveMaladieServiceImpl(EleveRepository eleveRepository, EleveMaladieRepository eleveMaladieRepository) {
		this.eleveRepository = eleveRepository;
		this.eleveMaladieRepository = eleveMaladieRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MaladieEleveListDto> listerFiltres(Long niveauId, Long classeId, String q) {
		return eleveMaladieRepository.findAll(EleveMaladieSpecification.filtres(niveauId, classeId, q)).stream()
				.map(MaladieEleveListDto::from).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<MaladieEleveDto> lister(Long eleveId) {
		verifierEleve(eleveId);
		return eleveMaladieRepository.findByEleveIdOrderByTypeAscLibelleAsc(eleveId).stream().map(MaladieEleveDto::from)
				.toList();
	}

	@Override
	@Transactional
	public MaladieEleveDto creer(Long eleveId, MaladieEleveRequest request) {
		Eleve e = eleveRepository.findById(eleveId).orElseThrow(() -> new BusinessException("Élève introuvable"));
		EleveMaladie m = new EleveMaladie();
		m.setEleve(e);
		m.setType(request.getType());
		m.setLibelle(request.getLibelle().trim());
		m.setDetails(trimToNull(request.getDetails()));
		return MaladieEleveDto.from(eleveMaladieRepository.save(m));
	}

	@Override
	@Transactional
	public MaladieEleveDto modifier(Long eleveId, Long id, MaladieEleveRequest request) {
		EleveMaladie m = eleveMaladieRepository.findByIdAndEleveId(id, eleveId)
				.orElseThrow(() -> new BusinessException("Entrée introuvable"));
		m.setType(request.getType());
		m.setLibelle(request.getLibelle().trim());
		m.setDetails(trimToNull(request.getDetails()));
		return MaladieEleveDto.from(eleveMaladieRepository.save(m));
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
}
