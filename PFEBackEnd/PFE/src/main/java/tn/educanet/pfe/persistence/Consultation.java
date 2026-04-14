package tn.educanet.pfe.persistence;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "consultation")
public class Consultation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eleve_id", nullable = false)
	private Eleve eleve;

	@Column(name = "date_consultation", nullable = false)
	private LocalDate dateConsultation;

	/** Température (ex. 37,5 °C). */
	@Column(name = "temperature")
	private Double temperature;

	@Column(name = "vomissement", nullable = false)
	private boolean vomissement;

	@Column(name = "diarrhee", nullable = false)
	private boolean diarrhee;

	@Column(name = "rapport", length = 2000)
	private String rapport;

	@Column(name = "prochaine_consultation")
	private LocalDate prochaineConsultation;

	/** Soins / médicaments prescrits (obligatoire en saisie). Colonne SQL historique {@code note}. */
	@Column(name = "note", length = 2000)
	private String traitement;

	public Consultation() {
	}

	public Consultation(Long id, Eleve eleve, LocalDate dateConsultation, Double temperature, boolean vomissement,
			boolean diarrhee, String rapport, LocalDate prochaineConsultation, String traitement) {
		this.id = id;
		this.eleve = eleve;
		this.dateConsultation = dateConsultation;
		this.temperature = temperature;
		this.vomissement = vomissement;
		this.diarrhee = diarrhee;
		this.rapport = rapport;
		this.prochaineConsultation = prochaineConsultation;
		this.traitement = traitement;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Eleve getEleve() {
		return eleve;
	}

	public void setEleve(Eleve eleve) {
		this.eleve = eleve;
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
