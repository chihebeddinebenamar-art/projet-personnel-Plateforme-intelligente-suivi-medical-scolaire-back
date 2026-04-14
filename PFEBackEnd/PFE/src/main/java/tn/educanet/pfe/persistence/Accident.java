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
@Table(name = "accident")
public class Accident implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eleve_id", nullable = false)
	private Eleve eleve;

	@Column(name = "date_accident", nullable = false)
	private LocalDate dateAccident;

	/** Description de l’acte. */
	@Column(length = 2000)
	private String description;

	@Column(length = 2000)
	private String diagnostic;

	@Column(length = 2000)
	private String traitement;

	@Column(length = 500)
	private String etat;

	public Accident() {
	}

	public Accident(Long id, Eleve eleve, LocalDate dateAccident, String description, String diagnostic,
			String traitement, String etat) {
		this.id = id;
		this.eleve = eleve;
		this.dateAccident = dateAccident;
		this.description = description;
		this.diagnostic = diagnostic;
		this.traitement = traitement;
		this.etat = etat;
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
