package tn.educanet.pfe.api.dto;

import tn.educanet.pfe.persistence.EleveMaladie;
import tn.educanet.pfe.persistence.TypeMaladieEleve;

public class MaladieEleveListDto {

	private Long id;
	private Long eleveId;
	private TypeMaladieEleve type;
	private String libelle;
	private String details;
	private String eleveNomComplet;
	private String classeNom;
	private String niveauNom;

	public MaladieEleveListDto() {
	}

	public MaladieEleveListDto(Long id, Long eleveId, TypeMaladieEleve type, String libelle, String details,
			String eleveNomComplet, String classeNom, String niveauNom) {
		this.id = id;
		this.eleveId = eleveId;
		this.type = type;
		this.libelle = libelle;
		this.details = details;
		this.eleveNomComplet = eleveNomComplet;
		this.classeNom = classeNom;
		this.niveauNom = niveauNom;
	}

	public static MaladieEleveListDto from(EleveMaladie m) {
		var e = m.getEleve();
		var cl = e.getClasse();
		var n = cl.getNiveau();
		String nom = e.getPrenom() + " " + e.getNom();
		return new MaladieEleveListDto(m.getId(), e.getId(), m.getType(), m.getLibelle(), m.getDetails(), nom,
				cl.getNom(), n.getNom());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEleveId() {
		return eleveId;
	}

	public void setEleveId(Long eleveId) {
		this.eleveId = eleveId;
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

	public String getNiveauNom() {
		return niveauNom;
	}

	public void setNiveauNom(String niveauNom) {
		this.niveauNom = niveauNom;
	}
}
