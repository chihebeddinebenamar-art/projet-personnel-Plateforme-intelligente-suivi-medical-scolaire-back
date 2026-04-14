package tn.educanet.pfe.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "niveau")
public class Niveau implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String nom;

	@Column(name = "annee_scolaire", length = 32)
	private String anneeScolaire;

	@OneToMany(mappedBy = "niveau", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Classe> classes = new ArrayList<>();

	public Niveau() {
	}

	public Niveau(Long id, String nom, String anneeScolaire, List<Classe> classes) {
		this.id = id;
		this.nom = nom;
		this.anneeScolaire = anneeScolaire;
		this.classes = classes;
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

	public String getAnneeScolaire() {
		return anneeScolaire;
	}

	public void setAnneeScolaire(String anneeScolaire) {
		this.anneeScolaire = anneeScolaire;
	}

	public List<Classe> getClasses() {
		return classes;
	}

	public void setClasses(List<Classe> classes) {
		this.classes = classes;
	}
}
