package tn.educanet.pfe.service;

public interface ElevePhotoProfilService {

	boolean hasImage(Long eleveId);

	String getContentType(Long eleveId);

	byte[] getImageBytes(Long eleveId);

	void upload(Long eleveId, String imageBase64);

	void supprimer(Long eleveId);

	void supprimerSiPresent(Long eleveId);
}
