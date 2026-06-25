package tn.educanet.pfe.service;

import java.util.List;

import com.tn.educanet.pfe.api.niveaux.schema.NiveauDto;
import com.tn.educanet.pfe.api.niveaux.schema.NiveauRequest;

public interface NiveauService {

	List<NiveauDto> lister(String annee, String nom);

	NiveauDto creer(NiveauRequest request);

	NiveauDto modifier(Long id, NiveauRequest request);

	void supprimer(Long id);

	NiveauDto get(Long id);
}
