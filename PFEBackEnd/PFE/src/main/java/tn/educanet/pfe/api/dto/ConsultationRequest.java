package tn.educanet.pfe.api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ConsultationRequest {

	@NotNull
	private Long eleveId;
	@NotNull
	private LocalDate dateConsultation;
	@NotNull
	private Double temperature;
	private Boolean vomissement;
	private Boolean diarrhee;
	private String rapport;
	private LocalDate prochaineConsultation;
	@NotBlank
	private String traitement;

	public ConsultationRequest() {
	}

	public Long getEleveId() {
		return eleveId;
	}

	public void setEleveId(Long eleveId) {
		this.eleveId = eleveId;
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

	public Boolean getVomissement() {
		return vomissement;
	}

	public void setVomissement(Boolean vomissement) {
		this.vomissement = vomissement;
	}

	public Boolean getDiarrhee() {
		return diarrhee;
	}

	public void setDiarrhee(Boolean diarrhee) {
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
