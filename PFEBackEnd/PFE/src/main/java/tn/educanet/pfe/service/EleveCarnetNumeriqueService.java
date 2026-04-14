package tn.educanet.pfe.service;

import tn.educanet.pfe.api.dto.CarnetNumeriqueUploadRequest;

public interface EleveCarnetNumeriqueService {

	boolean hasImage(Long eleveId);

	String getContentType(Long eleveId);

	byte[] getImageBytes(Long eleveId);

	void upload(Long eleveId, CarnetNumeriqueUploadRequest request);

	void supprimer(Long eleveId);

	/** Supprime le fichier et l’enregistrement si présent (avant suppression d’un élève). */
	void supprimerSiPresent(Long eleveId);
}
