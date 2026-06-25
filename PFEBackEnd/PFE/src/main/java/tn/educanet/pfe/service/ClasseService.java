package tn.educanet.pfe.service;

import java.util.List;

import com.tn.educanet.pfe.api.classes.schema.ClasseDto;
import com.tn.educanet.pfe.api.classes.schema.ClasseRequest;

public interface ClasseService {

	List<ClasseDto> lister(Long niveauId, String nom);

	List<ClasseDto> parNiveau(Long niveauId);

	ClasseDto creer(ClasseRequest request);

	ClasseDto modifier(Long id, ClasseRequest request);

	void supprimer(Long id);

	ClasseDto get(Long id);
}
