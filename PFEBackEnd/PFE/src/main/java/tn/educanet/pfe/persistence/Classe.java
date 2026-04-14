package tn.educanet.pfe.persistence;

import java.io.Serializable;
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

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "classe")
public class Classe implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String nom;

	/** Colonne historique MySQL (ancien « rang / année » du niveau), requise par le schéma existant. */
	@Column(name = "niveau_ordre", nullable = false)
	private int niveauOrdre;

	/**
	 * Données héritées : {@code niveau_id} peut pointer vers un niveau supprimé. Sans
	 * {@link NotFoundAction#IGNORE}, Hibernate lève une erreur à la liste des classes.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "niveau_id", nullable = false)
	private Niveau niveau;

	@OneToMany(mappedBy = "classe", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Eleve> eleves = new ArrayList<>();

	public Classe() {
	}

	public Classe(Long id, String nom, int niveauOrdre, Niveau niveau, List<Eleve> eleves) {
		this.id = id;
		this.nom = nom;
		this.niveauOrdre = niveauOrdre;
		this.niveau = niveau;
		this.eleves = eleves;
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

	public int getNiveauOrdre() {
		return niveauOrdre;
	}

	public void setNiveauOrdre(int niveauOrdre) {
		this.niveauOrdre = niveauOrdre;
	}

	public Niveau getNiveau() {
		return niveau;
	}

	public void setNiveau(Niveau niveau) {
		this.niveau = niveau;
	}

	public List<Eleve> getEleves() {
		return eleves;
	}

	public void setEleves(List<Eleve> eleves) {
		this.eleves = eleves;
	}
}
