package tn.educanet.pfe.api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AccidentRequest {

	@NotNull
	private Long eleveId;
	@NotNull
	private LocalDate dateAccident;
	@NotBlank
	private String description;
	@NotBlank
	private String diagnostic;
	@NotBlank
	private String traitement;
	@NotBlank
	private String etat;

	public AccidentRequest() {
	}

	public Long getEleveId() {
		return eleveId;
	}

	public void setEleveId(Long eleveId) {
		this.eleveId = eleveId;
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
