package tn.educanet.pfe.api.dto;

import tn.educanet.pfe.persistence.Classe;
import tn.educanet.pfe.persistence.Niveau;

public class ClasseDto {

	private Long id;
	private String nom;
	private Long niveauId;
	private String niveauNom;

	public ClasseDto() {
	}

	public ClasseDto(Long id, String nom, Long niveauId, String niveauNom) {
		this.id = id;
		this.nom = nom;
		this.niveauId = niveauId;
		this.niveauNom = niveauNom;
	}

	public static ClasseDto from(Classe c) {
		Niveau n = c.getNiveau();
		Long nid = n != null ? n.getId() : null;
		String nnom = n != null ? n.getNom() : null;
		return new ClasseDto(c.getId(), c.getNom(), nid, nnom);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Long getNiveauId() {
		return niveauId;
	}

	public void setNiveauId(Long niveauId) {
		this.niveauId = niveauId;
	}

	public String getNiveauNom() {
		return niveauNom;
	}

	public void setNiveauNom(String niveauNom) {
		this.niveauNom = niveauNom;
	}
}
