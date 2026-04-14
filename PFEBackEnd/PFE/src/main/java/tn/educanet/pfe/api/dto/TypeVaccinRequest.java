package tn.educanet.pfe.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class TypeVaccinRequest {

	@NotBlank
	private String nom;
	@Min(0)
	private int quantiteInitiale;

	public TypeVaccinRequest() {
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public int getQuantiteInitiale() {
		return quantiteInitiale;
	}

	public void setQuantiteInitiale(int quantiteInitiale) {
		this.quantiteInitiale = quantiteInitiale;
	}
}
