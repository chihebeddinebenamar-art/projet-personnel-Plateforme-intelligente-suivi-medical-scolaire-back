package tn.educanet.pfe.serviceimpl;

import java.util.List;

import org.dozer.Mapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;

import com.tn.educanet.pfe.api.classes.schema.ClasseDto;
import com.tn.educanet.pfe.api.classes.schema.ClasseRequest;

import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Classe;
import tn.educanet.pfe.persistence.Niveau;
import tn.educanet.pfe.repository.ClasseRepository;
import tn.educanet.pfe.repository.NiveauRepository;
import tn.educanet.pfe.service.ClasseService;

@Service
public class ClasseServiceImpl implements ClasseService {

	@Resource
	private ClasseRepository classeRepository;

	@Resource
	private NiveauRepository niveauRepository;
	@Resource
	private Mapper mapper;

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
		return list.stream().map(c -> mapper.map(c, ClasseDto.class)).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ClasseDto> parNiveau(Long niveauId) {
		return classeRepository.findByNiveauIdOrderByNomAsc(niveauId).stream()
				.map(c -> mapper.map(c, ClasseDto.class)).toList();
	}

	@Override
	@Transactional
	public ClasseDto creer(ClasseRequest request) {
		Niveau niveau = niveauRepository.findById(request.getNiveauId())
				.orElseThrow(() -> new BusinessException("Niveau introuvable"));
		String nom = request.getNom() != null ? request.getNom().trim() : null;
		if (!StringUtils.hasText(nom)) {
			throw new BusinessException("Le nom de la classe est obligatoire.");
		}
		if (classeRepository.existsByNomIgnoreCaseAndNiveauId(nom, request.getNiveauId())) {
			throw new BusinessException("Cette classe existe deja dans ce niveau.");
		}
		Classe c = new Classe();
		c.setNom(nom);
		c.setNiveau(niveau);
		c.setNiveauOrdre(niveauOrdrePourColonneLegacy(niveau));
		return mapper.map(classeRepository.save(c), ClasseDto.class);
	}

	@Override
	@Transactional
	public ClasseDto modifier(Long id, ClasseRequest request) {
		Classe c = classeRepository.findByIdFetchNiveau(id).orElseThrow(() -> new BusinessException("Classe introuvable"));
		Niveau niveau = niveauRepository.findById(request.getNiveauId())
				.orElseThrow(() -> new BusinessException("Niveau introuvable"));
		String nom = request.getNom() != null ? request.getNom().trim() : null;
		if (!StringUtils.hasText(nom)) {
			throw new BusinessException("Le nom de la classe est obligatoire.");
		}
		if (classeRepository.existsByNomIgnoreCaseAndNiveauIdAndIdNot(nom, request.getNiveauId(), id)) {
			throw new BusinessException("Une autre classe existe deja avec le meme nom dans ce niveau.");
		}
		c.setNom(nom);
		c.setNiveau(niveau);
		c.setNiveauOrdre(niveauOrdrePourColonneLegacy(niveau));
		return mapper.map(classeRepository.save(c), ClasseDto.class);
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
		return mapper.map(c, ClasseDto.class);
	}
}
