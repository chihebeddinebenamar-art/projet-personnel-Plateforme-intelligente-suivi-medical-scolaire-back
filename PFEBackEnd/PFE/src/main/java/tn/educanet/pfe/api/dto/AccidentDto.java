package tn.educanet.pfe.api.dto;

import java.time.LocalDate;

import tn.educanet.pfe.persistence.Accident;

public class AccidentDto {

	private Long id;
	private Long eleveId;
	private String eleveNomComplet;
	/** Affichage liste (scolarité). */
	private String classeNom;
	private String niveauNom;
	private LocalDate dateAccident;
	private String description;
	private String diagnostic;
	private String traitement;
	private String etat;

	public AccidentDto() {
	}

	public AccidentDto(Long id, Long eleveId, String eleveNomComplet, String classeNom, String niveauNom,
			LocalDate dateAccident, String description, String diagnostic, String traitement, String etat) {
		this.id = id;
		this.eleveId = eleveId;
		this.eleveNomComplet = eleveNomComplet;
		this.classeNom = classeNom;
		this.niveauNom = niveauNom;
		this.dateAccident = dateAccident;
		this.description = description;
		this.diagnostic = diagnostic;
		this.traitement = traitement;
		this.etat = etat;
	}

	public static AccidentDto from(Accident a) {
		var e = a.getEleve();
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
		return new AccidentDto(a.getId(), e.getId(), nom, cn, nn, a.getDateAccident(), a.getDescription(),
				a.getDiagnostic(), a.getTraitement(), a.getEtat());
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

	public LocalDate getDateAccident() {
		return dateAccident;
	}

	public void setDateAccident(LocalDate dateAccident) {
		this.dateAccident = dateAccident;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDiagnostic() {
		return diagnostic;
	}

	public void setDiagnostic(String diagnostic) {
		this.diagnostic = diagnostic;
	}

	public String getTraitement() {
		return traitement;
	}

	public void setTraitement(String traitement) {
		this.traitement = traitement;
	}

	public String getEtat() {
		return etat;
	}

	public void setEtat(String etat) {
		this.etat = etat;
	}
}
