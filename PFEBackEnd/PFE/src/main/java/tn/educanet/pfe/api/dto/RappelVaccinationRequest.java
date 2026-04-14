package tn.educanet.pfe.api.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RappelVaccinationRequest {

	@NotNull
	private LocalDate datePrevue;

	@NotNull
	private Long typeVaccinId;

	@NotEmpty
	private List<Long> classeIds;

	@Size(max = 2000)
	private String remarque;

	public RappelVaccinationRequest() {
	}

	public LocalDate getDatePrevue() {
		return datePrevue;
	}

	public void setDatePrevue(LocalDate datePrevue) {
		this.datePrevue = datePrevue;
	}

	public Long getTypeVaccinId() {
		return typeVaccinId;
	}

	public void setTypeVaccinId(Long typeVaccinId) {
		this.typeVaccinId = typeVaccinId;
	}

	public List<Long> getClasseIds() {
		return classeIds;
	}

	public void setClasseIds(List<Long> classeIds) {
		this.classeIds = classeIds;
	}

	public String getRemarque() {
		return remarque;
	}

	public void setRemarque(String remarque) {
		this.remarque = remarque;
	}
}
