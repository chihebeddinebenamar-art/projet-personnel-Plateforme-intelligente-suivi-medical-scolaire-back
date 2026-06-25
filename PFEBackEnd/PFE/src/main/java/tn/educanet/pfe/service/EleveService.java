package tn.educanet.pfe.service;

import java.util.List;

import com.tn.educanet.pfe.api.eleves.schema.EleveDto;
import com.tn.educanet.pfe.api.eleves.schema.EleveRequest;
import com.tn.educanet.pfe.api.eleves.schema.FicheMedicaleDto;

public interface EleveService {

	List<EleveDto> listerFiltres(Long niveauId, Long classeId, String recherche);

	List<EleveDto> parClasse(Long classeId);

	EleveDto creer(EleveRequest request);

	EleveDto modifier(Long id, EleveRequest request);

	void supprimer(Long id);

	EleveDto get(Long id);

	FicheMedicaleDto ficheMedicale(Long eleveId);

	/** Vérifie numéro parent (normalisé) + mot de passe en clair contre le hash stocké sur un élève. */
	boolean verifierCredentialsParent(String numeroTelephone, String motDePassePlain);

	/**
	 * Change le mot de passe parent sur tous les élèves partageant ce numéro (après vérification de
	 * l’ancien mot de passe).
	 */
	ChangeParentPasswordResult changerMotDePasseParent(String numeroTelephone, String ancienPlain, String nouveauPlain);
}
