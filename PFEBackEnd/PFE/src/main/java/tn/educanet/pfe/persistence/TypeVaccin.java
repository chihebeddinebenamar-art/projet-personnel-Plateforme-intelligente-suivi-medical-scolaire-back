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
@Table(name = "type_vaccin")
public class TypeVaccin implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String nom;

	@Column(name = "quantite_totale", nullable = false)
	private int quantiteTotale;

	@OneToMany(mappedBy = "typeVaccin", cascade = CascadeType.ALL)
	private List<Vaccination> vaccinations = new ArrayList<>();

	public TypeVaccin() {
	}

	public TypeVaccin(Long id, String nom, int quantiteTotale, List<Vaccination> vaccinations) {
		this.id = id;
		this.nom = nom;
		this.quantiteTotale = quantiteTotale;
		this.vaccinations = vaccinations;
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

	public int getQuantiteTotale() {
		return quantiteTotale;
	}

	public void setQuantiteTotale(int quantiteTotale) {
		this.quantiteTotale = quantiteTotale;
	}

	public List<Vaccination> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(List<Vaccination> vaccinations) {
		this.vaccinations = vaccinations;
	}
}
