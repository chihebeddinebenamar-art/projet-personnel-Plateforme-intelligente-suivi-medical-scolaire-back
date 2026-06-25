package tn.educanet.pfe.service;

import java.util.List;

public interface EleveCarnetNumeriqueService {

	boolean hasImage(Long eleveId);

	String getContentType(Long eleveId);

	byte[] getImageBytes(Long eleveId);

	byte[] getImageBytes(Long eleveId, Long photoId);

	List<Long> listPhotoIds(Long eleveId);

	String getDescription(Long eleveId);

	/** photoId null : première image (ou image unique sur la ligne carnet). */
	String resolveContentType(Long eleveId, Long photoId);

	void upload(Long eleveId, String imageBase64, String description);

	void supprimer(Long eleveId);

	/** Supprime une photo du carnet (Cloudinary + ligne) et réindexe {@code sort_index}. */
	void supprimerPhoto(Long eleveId, Long photoId);

	/** Supprime le fichier et l’enregistrement si présent (avant suppression d’un élève). */
	void supprimerSiPresent(Long eleveId);

	/** Migre l’ancienne image unique (ligne carnet) vers la table des photos si besoin. */
	void syncLegacyCarnetPhotos(Long eleveId);
}
