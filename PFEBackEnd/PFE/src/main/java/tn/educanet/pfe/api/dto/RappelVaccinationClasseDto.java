package tn.educanet.pfe.api.dto;

public class RappelVaccinationClasseDto {

	private Long id;
	private String nom;
	private Long niveauId;
	private String niveauNom;

	public RappelVaccinationClasseDto() {
	}

	public RappelVaccinationClasseDto(Long id, String nom, Long niveauId, String niveauNom) {
		this.id = id;
		this.nom = nom;
		this.niveauId = niveauId;
		this.niveauNom = niveauNom;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
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
