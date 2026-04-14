package tn.educanet.pfe.persistence;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "eleve")
public class Eleve implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 64, unique = true)
	private String matricule;

	@Column(nullable = false, length = 120)
	private String nom;

	@Column(nullable = false, length = 120)
	private String prenom;

	@Column(name = "date_naissance")
	private LocalDate dateNaissance;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "classe_id", nullable = false)
	private Classe classe;

	@OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Vaccination> vaccinations = new ArrayList<>();

	@OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Consultation> consultations = new ArrayList<>();

	@OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Accident> accidents = new ArrayList<>();

	@OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EleveMaladie> maladies = new ArrayList<>();

	public Eleve() {
	}

	public Eleve(Long id, String matricule, String nom, String prenom, LocalDate dateNaissance, Classe classe,
			List<Vaccination> vaccinations, List<Consultation> consultations, List<Accident> accidents,
			List<EleveMaladie> maladies) {
		this.id = id;
		this.matricule = matricule;
		this.nom = nom;
		this.prenom = prenom;
		this.dateNaissance = dateNaissance;
		this.classe = classe;
		this.vaccinations = vaccinations;
		this.consultations = consultations;
		this.accidents = accidents;
		this.maladies = maladies;
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

	public Classe getClasse() {
		return classe;
	}

	public void setClasse(Classe classe) {
		this.classe = classe;
	}

	public List<Vaccination> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(List<Vaccination> vaccinations) {
		this.vaccinations = vaccinations;
	}

	public List<Consultation> getConsultations() {
		return consultations;
	}

	public void setConsultations(List<Consultation> consultations) {
		this.consultations = consultations;
	}

	public List<Accident> getAccidents() {
		return accidents;
	}

	public void setAccidents(List<Accident> accidents) {
		this.accidents = accidents;
	}

	public List<EleveMaladie> getMaladies() {
		return maladies;
	}

	public void setMaladies(List<EleveMaladie> maladies) {
		this.maladies = maladies;
	}
}
