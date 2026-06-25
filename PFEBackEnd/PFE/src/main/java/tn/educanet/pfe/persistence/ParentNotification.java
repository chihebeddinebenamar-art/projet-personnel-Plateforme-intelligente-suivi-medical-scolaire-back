package tn.educanet.pfe.persistence;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "parent_notification")
public class ParentNotification implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "numero_parent", nullable = false, length = 32)
	private String numeroParent;

	@Column(name = "eleve_id")
	private Long eleveId;

	@Column(name = "eleve_nom_complet", length = 240)
	private String eleveNomComplet;

	@Column(name = "classe_nom", length = 160)
	private String classeNom;

	@Column(name = "titre", nullable = false, length = 160)
	private String titre;

	@Column(name = "message", nullable = false, length = 2000)
	private String message;

	@Column(name = "type_evenement", nullable = false, length = 40)
	private String typeEvenement;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void onCreate() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumeroParent() {
		return numeroParent;
	}

	public void setNumeroParent(String numeroParent) {
		this.numeroParent = numeroParent;
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

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTypeEvenement() {
		return typeEvenement;
	}

	public void setTypeEvenement(String typeEvenement) {
		this.typeEvenement = typeEvenement;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
