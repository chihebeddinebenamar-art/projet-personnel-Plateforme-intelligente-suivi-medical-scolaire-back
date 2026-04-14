package tn.educanet.pfe.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ClasseRequest {

	@NotBlank
	private String nom;
	@NotNull
	private Long niveauId;

	public ClasseRequest() {
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
}
