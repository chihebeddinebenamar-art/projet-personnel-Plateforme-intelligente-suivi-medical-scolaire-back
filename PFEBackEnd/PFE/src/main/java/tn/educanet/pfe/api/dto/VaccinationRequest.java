package tn.educanet.pfe.api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public class VaccinationRequest {

	@NotNull
	private Long eleveId;
	@NotNull
	private Long typeVaccinId;
	/** Date à laquelle la vaccination a été réalisée. */
	@NotNull
	private LocalDate dateVaccination;
	/** Date prévue (optionnel). */
	private LocalDate datePrevue;
	/** Numéro de lot du flacon utilisé. */
	private String numeroLot;

	public VaccinationRequest() {
	}

	public Long getEleveId() {
		return eleveId;
	}

	public void setEleveId(Long eleveId) {
		this.eleveId = eleveId;
	}

	public Long getTypeVaccinId() {
		return typeVaccinId;
	}

	public void setTypeVaccinId(Long typeVaccinId) {
		this.typeVaccinId = typeVaccinId;
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
