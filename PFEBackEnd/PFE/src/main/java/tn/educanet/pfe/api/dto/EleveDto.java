package tn.educanet.pfe.api.dto;

import java.time.LocalDate;

import tn.educanet.pfe.persistence.Eleve;

public class EleveDto {

	private Long id;
	private String matricule;
	private String nom;
	private String prenom;
	private LocalDate dateNaissance;
	private Long classeId;
	private String classeNom;
	private Long niveauId;
	private String niveauNom;

	public EleveDto() {
	}

	public EleveDto(Long id, String matricule, String nom, String prenom, LocalDate dateNaissance, Long classeId,
			String classeNom, Long niveauId, String niveauNom) {
		this.id = id;
		this.matricule = matricule;
		this.nom = nom;
		this.prenom = prenom;
		this.dateNaissance = dateNaissance;
		this.classeId = classeId;
		this.classeNom = classeNom;
		this.niveauId = niveauId;
		this.niveauNom = niveauNom;
	}

	public static EleveDto from(Eleve e) {
		return new EleveDto(e.getId(), e.getMatricule(), e.getNom(), e.getPrenom(), e.getDateNaissance(),
				e.getClasse().getId(), e.getClasse().getNom(), e.getClasse().getNiveau().getId(),
				e.getClasse().getNiveau().getNom());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMatricule() {
		return matricule;
	}

	public void setMatricule(String matricule) {
		this.matricule = matricule;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public LocalDate getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(LocalDate dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	public Long getClasseId() {
		return classeId;
	}

	public void setClasseId(Long classeId) {
		this.classeId = classeId;
	}

	public String getClasseNom() {
		return classeNom;
	}

	public void setClasseNom(String classeNom) {
		this.classeNom = classeNom;
	}

	public Long getNiveauId() {
		return niveauId;
	}

	public void setNiveauId(Long niveauId) {
		this.niveauId = niveauId;
	}

	public String getNiveauNom() {
		return niveauNom;
	}

	public void setNiveauNom(String niveauNom) {
		this.niveauNom = niveauNom;
	}
}
