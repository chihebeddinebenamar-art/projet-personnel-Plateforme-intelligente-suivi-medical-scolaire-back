package tn.educanet.pfe.api.dto;

import java.time.LocalDate;

import tn.educanet.pfe.persistence.Consultation;

public class ConsultationDto {

	private Long id;
	private Long eleveId;
	private String eleveNomComplet;
	/** Affichage liste (scolarité). */
	private String classeNom;
	private String niveauNom;
	private LocalDate dateConsultation;
	private Double temperature;
	private boolean vomissement;
	private boolean diarrhee;
	private String rapport;
	private LocalDate prochaineConsultation;
	private String traitement;

	public ConsultationDto() {
	}

	public ConsultationDto(Long id, Long eleveId, String eleveNomComplet, String classeNom, String niveauNom,
			LocalDate dateConsultation, Double temperature, boolean vomissement, boolean diarrhee, String rapport,
			LocalDate prochaineConsultation, String traitement) {
		this.id = id;
		this.eleveId = eleveId;
		this.eleveNomComplet = eleveNomComplet;
		this.classeNom = classeNom;
		this.niveauNom = niveauNom;
		this.dateConsultation = dateConsultation;
		this.temperature = temperature;
		this.vomissement = vomissement;
		this.diarrhee = diarrhee;
		this.rapport = rapport;
		this.prochaineConsultation = prochaineConsultation;
		this.traitement = traitement;
	}

	public static ConsultationDto from(Consultation c) {
		var e = c.getEleve();
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
		return new ConsultationDto(c.getId(), e.getId(), nom, cn, nn, c.getDateConsultation(), c.getTemperature(),
				c.isVomissement(), c.isDiarrhee(), c.getRapport(), c.getProchaineConsultation(), c.getTraitement());
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

	public LocalDate getDateConsultation() {
		return dateConsultation;
	}

	public void setDateConsultation(LocalDate dateConsultation) {
		this.dateConsultation = dateConsultation;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public boolean isVomissement() {
		return vomissement;
	}

	public void setVomissement(boolean vomissement) {
		this.vomissement = vomissement;
	}

	public boolean isDiarrhee() {
		return diarrhee;
	}

	public void setDiarrhee(boolean diarrhee) {
		this.diarrhee = diarrhee;
	}

	public String getRapport() {
		return rapport;
	}

	public void setRapport(String rapport) {
		this.rapport = rapport;
	}

	public LocalDate getProchaineConsultation() {
		return prochaineConsultation;
	}

	public void setProchaineConsultation(LocalDate prochaineConsultation) {
		this.prochaineConsultation = prochaineConsultation;
	}

	public String getTraitement() {
		return traitement;
	}

	public void setTraitement(String traitement) {
		this.traitement = traitement;
	}
}
