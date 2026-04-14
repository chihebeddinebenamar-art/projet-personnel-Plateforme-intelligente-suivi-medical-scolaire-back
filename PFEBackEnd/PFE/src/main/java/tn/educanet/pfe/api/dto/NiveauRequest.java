package tn.educanet.pfe.api.dto;

import jakarta.validation.constraints.NotBlank;

public class NiveauRequest {

	@NotBlank
	private String nom;
	private String anneeScolaire;

	public NiveauRequest() {
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
