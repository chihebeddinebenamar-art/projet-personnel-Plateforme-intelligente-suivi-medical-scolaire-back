package tn.educanet.pfe.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tn.educanet.pfe.persistence.TypeMaladieEleve;

public class MaladieEleveRequest {

	@NotNull
	private TypeMaladieEleve type;

	@NotBlank
	@Size(max = 255)
	private String libelle;

	@Size(max = 2000)
	private String details;

	public MaladieEleveRequest() {
	}

	public TypeMaladieEleve getType() {
		return type;
	}

	public void setType(TypeMaladieEleve type) {
		this.type = type;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}
