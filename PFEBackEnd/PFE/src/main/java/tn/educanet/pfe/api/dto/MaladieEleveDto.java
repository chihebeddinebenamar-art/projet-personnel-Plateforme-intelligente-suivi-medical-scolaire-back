package tn.educanet.pfe.api.dto;

import tn.educanet.pfe.persistence.EleveMaladie;
import tn.educanet.pfe.persistence.TypeMaladieEleve;

public class MaladieEleveDto {

	private Long id;
	private Long eleveId;
	private TypeMaladieEleve type;
	private String libelle;
	private String details;

	public MaladieEleveDto() {
	}

	public MaladieEleveDto(Long id, Long eleveId, TypeMaladieEleve type, String libelle, String details) {
		this.id = id;
		this.eleveId = eleveId;
		this.type = type;
		this.libelle = libelle;
		this.details = details;
	}

	public static MaladieEleveDto from(EleveMaladie m) {
		return new MaladieEleveDto(m.getId(), m.getEleve().getId(), m.getType(), m.getLibelle(), m.getDetails());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEleveId() {
		return eleveId;
	}

	public void setEleveId(Long eleveId) {
		this.eleveId = eleveId;
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
