package tn.educanet.pfe.api.dto;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import tn.educanet.pfe.persistence.Classe;
import tn.educanet.pfe.persistence.RappelVaccination;

public class RappelVaccinationDto {

	private Long id;
	private LocalDate datePrevue;
	private Long typeVaccinId;
	private String typeVaccinNom;
	private List<RappelVaccinationClasseDto> classes;
	private String remarque;

	public RappelVaccinationDto() {
	}

	public RappelVaccinationDto(Long id, LocalDate datePrevue, Long typeVaccinId, String typeVaccinNom,
			List<RappelVaccinationClasseDto> classes, String remarque) {
		this.id = id;
		this.datePrevue = datePrevue;
		this.typeVaccinId = typeVaccinId;
		this.typeVaccinNom = typeVaccinNom;
		this.classes = classes;
		this.remarque = remarque;
	}

	public static RappelVaccinationDto from(RappelVaccination r) {
		List<RappelVaccinationClasseDto> cls = r.getClasses().stream()
				.sorted(Comparator.comparing(Classe::getNom, Comparator.nullsLast(String::compareToIgnoreCase)))
				.map(c -> {
					Long nid = null;
					String nn = null;
					if (c.getNiveau() != null) {
						nid = c.getNiveau().getId();
						nn = c.getNiveau().getNom();
					}
					return new RappelVaccinationClasseDto(c.getId(), c.getNom(), nid, nn);
				}).toList();
		return new RappelVaccinationDto(r.getId(), r.getDatePrevue(), r.getTypeVaccin().getId(), r.getTypeVaccin().getNom(),
				cls, r.getRemarque());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getTypeVaccinNom() {
		return typeVaccinNom;
	}

	public void setTypeVaccinNom(String typeVaccinNom) {
		this.typeVaccinNom = typeVaccinNom;
	}

	public List<RappelVaccinationClasseDto> getClasses() {
		return classes;
	}

	public void setClasses(List<RappelVaccinationClasseDto> classes) {
		this.classes = classes;
	}

	public String getRemarque() {
		return remarque;
	}

	public void setRemarque(String remarque) {
		this.remarque = remarque;
	}
}
