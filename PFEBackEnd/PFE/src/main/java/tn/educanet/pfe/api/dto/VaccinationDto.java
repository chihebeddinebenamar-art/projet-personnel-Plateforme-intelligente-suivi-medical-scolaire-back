package tn.educanet.pfe.api.dto;

import java.time.LocalDate;

import tn.educanet.pfe.persistence.Vaccination;

public class VaccinationDto {

	private Long id;
	private Long eleveId;
	private String eleveNomComplet;
	/** Affichage liste (scolarité). */
	private String classeNom;
	private String niveauNom;
	private Long typeVaccinId;
	private String typeVaccinNom;
	private int dose;
	private LocalDate dateVaccination;
	private LocalDate datePrevue;
	private String numeroLot;

	public VaccinationDto() {
	}

	public VaccinationDto(Long id, Long eleveId, String eleveNomComplet, String classeNom, String niveauNom,
			Long typeVaccinId, String typeVaccinNom, int dose, LocalDate dateVaccination, LocalDate datePrevue,
			String numeroLot) {
		this.id = id;
		this.eleveId = eleveId;
		this.eleveNomComplet = eleveNomComplet;
		this.classeNom = classeNom;
		this.niveauNom = niveauNom;
		this.typeVaccinId = typeVaccinId;
		this.typeVaccinNom = typeVaccinNom;
		this.dose = dose;
		this.dateVaccination = dateVaccination;
		this.datePrevue = datePrevue;
		this.numeroLot = numeroLot;
	}

	public static VaccinationDto from(Vaccination v) {
		var e = v.getEleve();
		String nom = e.getPrenom() + " " + e.getNom();
		String cn = null;
		String nn = null;
		var cl = e.getClasse();
		if (cl != null) {
			cn = cl.getNom();
			if (cl.getNiveau() != null) {
				nn = cl.getNiveau().getNom();
			}
		}
		return new VaccinationDto(v.getId(), e.getId(), nom, cn, nn, v.getTypeVaccin().getId(),
				v.getTypeVaccin().getNom(), v.getDose(), v.getDateVaccination(), v.getDatePrevue(), v.getNumeroLot());
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

	public String getEleveNomComplet() {
		return eleveNomComplet;
	}

	public void setEleveNomComplet(String eleveNomComplet) {
		this.eleveNomComplet = eleveNomComplet;
	}

	public String getClasseNom() {
		return classeNom;
	}

	public void setClasseNom(String classeNom) {
		this.classeNom = classeNom;
	}

	public String getNiveauNom() {
		return niveauNom;
	}

	public void setNiveauNom(String niveauNom) {
		this.niveauNom = niveauNom;
	}

	public Long getTypeVaccinId() {
		return typeVaccinId;
	}

	public void setTypeVaccinId(Long typeVaccinId) {
		this.typeVaccinId = typeVaccinId;
	}

	public String getTypeVaccinNom() {
		return typeVaccinNom;
	}

	public void setTypeVaccinNom(String typeVaccinNom) {
		this.typeVaccinNom = typeVaccinNom;
	}

	public int getDose() {
		return dose;
	}

	public void setDose(int dose) {
		this.dose = dose;
	}

	public LocalDate getDateVaccination() {
		return dateVaccination;
	}

	public void setDateVaccination(LocalDate dateVaccination) {
		this.dateVaccination = dateVaccination;
	}

	public LocalDate getDatePrevue() {
		return datePrevue;
	}

	public void setDatePrevue(LocalDate datePrevue) {
		this.datePrevue = datePrevue;
	}

	public String getNumeroLot() {
		return numeroLot;
	}

	public void setNumeroLot(String numeroLot) {
		this.numeroLot = numeroLot;
	}
}
