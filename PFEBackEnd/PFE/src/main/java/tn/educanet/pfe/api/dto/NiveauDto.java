package tn.educanet.pfe.api.dto;

import tn.educanet.pfe.persistence.Niveau;

public class NiveauDto {

	private Long id;
	private String nom;
	private String anneeScolaire;

	public NiveauDto() {
	}

	public NiveauDto(Long id, String nom, String anneeScolaire) {
		this.id = id;
		this.nom = nom;
		this.anneeScolaire = anneeScolaire;
	}

	public static NiveauDto from(Niveau n) {
		return new NiveauDto(n.getId(), n.getNom(), n.getAnneeScolaire());
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

	public String getAnneeScolaire() {
		return anneeScolaire;
	}

	public void setAnneeScolaire(String anneeScolaire) {
		this.anneeScolaire = anneeScolaire;
	}
}
