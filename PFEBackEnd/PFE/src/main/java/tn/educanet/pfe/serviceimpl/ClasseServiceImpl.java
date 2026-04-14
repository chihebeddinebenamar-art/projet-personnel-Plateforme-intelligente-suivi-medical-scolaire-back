package tn.educanet.pfe.serviceimpl;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import tn.educanet.pfe.api.dto.ClasseDto;
import tn.educanet.pfe.api.dto.ClasseRequest;
import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Classe;
import tn.educanet.pfe.persistence.Niveau;
import tn.educanet.pfe.repository.ClasseRepository;
import tn.educanet.pfe.repository.NiveauRepository;
import tn.educanet.pfe.service.ClasseService;

@Service
public class ClasseServiceImpl implements ClasseService {

	private final ClasseRepository classeRepository;
	private final NiveauRepository niveauRepository;

	public ClasseServiceImpl(ClasseRepository classeRepository, NiveauRepository niveauRepository) {
		this.classeRepository = classeRepository;
		this.niveauRepository = niveauRepository;
	}

	private static int niveauOrdrePourColonneLegacy(Niveau niveau) {
		Long id = niveau.getId();
		if (id == null) {
			return 0;
		}
		long v = id;
		return v > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) v;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ClasseDto> lister(Long niveauId, String nom) {
		List<Classe> list;
		if (niveauId != null) {
			list = classeRepository.findByNiveauIdOrderByNomAsc(niveauId);
		} else {
			list = classeRepository.findAllFetchNiveau();
		}
		if (StringUtils.hasText(nom)) {
			String n = nom.trim().toLowerCase();
			list = list.stream()
					.filter(c -> c.getNom() != null && c.getNom().toLowerCase().contains(n))
					.toList();
		}
		return list.stream().map(ClasseDto::from).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ClasseDto> parNiveau(Long niveauId) {
		return classeRepository.findByNiveauIdOrderByNomAsc(niveauId).stream().map(ClasseDto::from).toList();
	}

	@Override
	@Transactional
	public ClasseDto creer(ClasseRequest request) {
		Niveau niveau = niveauRepository.findById(request.getNiveauId())
				.orElseThrow(() -> new BusinessException("Niveau introuvable"));
		Classe c = new Classe();
		c.setNom(request.getNom());
		c.setNiveau(niveau);
		c.setNiveauOrdre(niveauOrdrePourColonneLegacy(niveau));
		return ClasseDto.from(classeRepository.save(c));
	}

	@Override
	@Transactional
	public ClasseDto modifier(Long id, ClasseRequest request) {
		Classe c = classeRepository.findByIdFetchNiveau(id).orElseThrow(() -> new BusinessException("Classe introuvable"));
		Niveau niveau = niveauRepository.findById(request.getNiveauId())
				.orElseThrow(() -> new BusinessException("Niveau introuvable"));
		c.setNom(request.getNom());
		c.setNiveau(niveau);
		c.setNiveauOrdre(niveauOrdrePourColonneLegacy(niveau));
		return ClasseDto.from(classeRepository.save(c));
	}

	@Override
	@Transactional
	public void supprimer(Long id) {
		Classe c = classeRepository.findById(id).orElseThrow(() -> new BusinessException("Classe introuvable"));
		try {
			classeRepository.delete(c);
		} catch (DataIntegrityViolationException e) {
			throw new BusinessException("Impossible de supprimer : des élèves (ou données liées) utilisent encore cette classe.");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ClasseDto get(Long id) {
		Classe c = classeRepository.findByIdFetchNiveau(id).orElseThrow(() -> new BusinessException("Classe introuvable"));
		return ClasseDto.from(c);
	}
}
