package tn.educanet.pfe.api.dto;

import tn.educanet.pfe.persistence.TypeVaccin;

public class TypeVaccinDto {

	private Long id;
	private String nom;
	private int quantiteTotale;

	public TypeVaccinDto() {
	}

	public TypeVaccinDto(Long id, String nom, int quantiteTotale) {
		this.id = id;
		this.nom = nom;
		this.quantiteTotale = quantiteTotale;
	}

	public static TypeVaccinDto from(TypeVaccin t) {
		return new TypeVaccinDto(t.getId(), t.getNom(), t.getQuantiteTotale());
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

	public int getQuantiteTotale() {
		return quantiteTotale;
	}

	public void setQuantiteTotale(int quantiteTotale) {
		this.quantiteTotale = quantiteTotale;
	}
}
