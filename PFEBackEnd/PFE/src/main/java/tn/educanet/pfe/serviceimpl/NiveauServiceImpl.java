package tn.educanet.pfe.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.educanet.pfe.api.dto.NiveauDto;
import tn.educanet.pfe.api.dto.NiveauRequest;
import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Niveau;
import tn.educanet.pfe.repository.NiveauRepository;
import tn.educanet.pfe.service.NiveauService;
import tn.educanet.pfe.specification.NiveauSpecification;

@Service
public class NiveauServiceImpl implements NiveauService {

	private final NiveauRepository niveauRepository;

	public NiveauServiceImpl(NiveauRepository niveauRepository) {
		this.niveauRepository = niveauRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<NiveauDto> lister(String annee, String nom) {
		return niveauRepository.findAll(NiveauSpecification.filtres(annee, nom)).stream().map(NiveauDto::from).toList();
	}

	@Override
	@Transactional
	public NiveauDto creer(NiveauRequest request) {
		Niveau n = new Niveau();
		n.setNom(request.getNom());
		n.setAnneeScolaire(request.getAnneeScolaire());
		return NiveauDto.from(niveauRepository.save(n));
	}

	@Override
	@Transactional
	public NiveauDto modifier(Long id, NiveauRequest request) {
		Niveau n = niveauRepository.findById(id).orElseThrow(() -> new BusinessException("Niveau introuvable"));
		n.setNom(request.getNom());
		n.setAnneeScolaire(request.getAnneeScolaire());
		return NiveauDto.from(niveauRepository.save(n));
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
		return niveauRepository.findById(id).map(NiveauDto::from)
				.orElseThrow(() -> new BusinessException("Niveau introuvable"));
	}
}
