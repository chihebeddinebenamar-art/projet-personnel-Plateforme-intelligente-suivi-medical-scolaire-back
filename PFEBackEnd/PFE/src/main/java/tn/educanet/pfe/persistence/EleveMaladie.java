package tn.educanet.pfe.persistence;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "eleve_maladie")
public class EleveMaladie implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "eleve_id", nullable = false)
	private Eleve eleve;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private TypeMaladieEleve type;

	@Column(nullable = false, length = 255)
	private String libelle;

	@Column(length = 2000)
	private String details;

	public EleveMaladie() {
	}

	public EleveMaladie(Long id, Eleve eleve, TypeMaladieEleve type, String libelle, String details) {
		this.id = id;
		this.eleve = eleve;
		this.type = type;
		this.libelle = libelle;
		this.details = details;
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

	public TypeMaladieEleve getType() {
		return type;
	}

	public void setType(TypeMaladieEleve type) {
		this.type = type;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}
