package tn.educanet.pfe.serviceimpl;

import java.util.List;

import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;

import com.tn.educanet.pfe.api.niveaux.schema.NiveauDto;
import com.tn.educanet.pfe.api.niveaux.schema.NiveauRequest;

import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Niveau;
import tn.educanet.pfe.repository.NiveauRepository;
import tn.educanet.pfe.service.NiveauService;
import tn.educanet.pfe.specification.NiveauSpecification;

@Service
public class NiveauServiceImpl implements NiveauService {

	@Resource
	private NiveauRepository niveauRepository;
	@Resource
	private Mapper mapper;

	@Override
	@Transactional(readOnly = true)
	public List<NiveauDto> lister(String annee, String nom) {
		return niveauRepository.findAll(NiveauSpecification.filtres(annee, nom)).stream()
				.map(n -> mapper.map(n, NiveauDto.class)).toList();
	}

	@Override
	@Transactional
	public NiveauDto creer(NiveauRequest request) {
		String nom = request.getNom() != null ? request.getNom().trim() : null;
		String annee = request.getAnneeScolaire() != null ? request.getAnneeScolaire().trim() : null;
		if (!StringUtils.hasText(nom)) {
			throw new BusinessException("Le nom du niveau est obligatoire.");
		}
		if (!StringUtils.hasText(annee)) {
			throw new BusinessException("L'annee scolaire du niveau est obligatoire.");
		}
		if (niveauRepository.existsByNomIgnoreCaseAndAnneeScolaireIgnoreCase(nom, annee)) {
			throw new BusinessException("Ce niveau existe deja pour cette annee scolaire.");
		}
		Niveau n = new Niveau();
		n.setNom(nom);
		n.setAnneeScolaire(annee);
		return mapper.map(niveauRepository.save(n), NiveauDto.class);
	}

	@Override
	@Transactional
	public NiveauDto modifier(Long id, NiveauRequest request) {
		Niveau n = niveauRepository.findById(id).orElseThrow(() -> new BusinessException("Niveau introuvable"));
		String nom = request.getNom() != null ? request.getNom().trim() : null;
		String annee = request.getAnneeScolaire() != null ? request.getAnneeScolaire().trim() : null;
		if (!StringUtils.hasText(nom)) {
			throw new BusinessException("Le nom du niveau est obligatoire.");
		}
		if (!StringUtils.hasText(annee)) {
			throw new BusinessException("L'annee scolaire du niveau est obligatoire.");
		}
		if (niveauRepository.existsByNomIgnoreCaseAndAnneeScolaireIgnoreCaseAndIdNot(nom, annee, id)) {
			throw new BusinessException("Un autre niveau existe deja avec le meme nom et la meme annee scolaire.");
		}
		n.setNom(nom);
		n.setAnneeScolaire(annee);
		return mapper.map(niveauRepository.save(n), NiveauDto.class);
	}

	@Override
	@Transactional
	public void supprimer(Long id) {
		if (!niveauRepository.existsById(id)) {
			throw new BusinessException("Niveau introuvable");
		}
		niveauRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public NiveauDto get(Long id) {
		return niveauRepository.findById(id).map(n -> mapper.map(n, NiveauDto.class))
				.orElseThrow(() -> new BusinessException("Niveau introuvable"));
	}
}
