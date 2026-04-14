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
@Table(name = "vaccination")
public class Vaccination implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eleve_id", nullable = false)
	private Eleve eleve;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_vaccin_id", nullable = false)
	private TypeVaccin typeVaccin;

	@Column(nullable = false)
	private int dose;

	/** Date à laquelle la vaccination a été réalisée. */
	@Column(name = "date_vaccination", nullable = false)
	private LocalDate dateVaccination;

	/** Date prévue (calendrier vaccinal / rappel). */
	@Column(name = "date_prevue")
	private LocalDate datePrevue;

	/** Numéro de lot du flacon utilisé pour cette administration. */
	@Column(name = "numero_lot", length = 128)
	private String numeroLot;

	public Vaccination() {
	}

	public Vaccination(Long id, Eleve eleve, TypeVaccin typeVaccin, int dose, LocalDate dateVaccination,
			LocalDate datePrevue, String numeroLot) {
		this.id = id;
		this.eleve = eleve;
		this.typeVaccin = typeVaccin;
		this.dose = dose;
		this.dateVaccination = dateVaccination;
		this.datePrevue = datePrevue;
		this.numeroLot = numeroLot;
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

	public TypeVaccin getTypeVaccin() {
		return typeVaccin;
	}

	public void setTypeVaccin(TypeVaccin typeVaccin) {
		this.typeVaccin = typeVaccin;
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
