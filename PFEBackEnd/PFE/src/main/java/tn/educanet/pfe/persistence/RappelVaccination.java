package tn.educanet.pfe.persistence;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rappel_vaccination")
public class RappelVaccination implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "date_prevue", nullable = false)
	private LocalDate datePrevue;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "type_vaccin_id", nullable = false)
	private TypeVaccin typeVaccin;

	@Column(length = 2000)
	private String remarque;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "rappel_vaccination_classe", joinColumns = @JoinColumn(name = "rappel_id"), inverseJoinColumns = @JoinColumn(name = "classe_id"))
	private Set<Classe> classes = new HashSet<>();

	public RappelVaccination() {
	}

	public RappelVaccination(Long id, LocalDate datePrevue, TypeVaccin typeVaccin, String remarque,
			Set<Classe> classes) {
		this.id = id;
		this.datePrevue = datePrevue;
		this.typeVaccin = typeVaccin;
		this.remarque = remarque;
		this.classes = classes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getDatePrevue() {
		return datePrevue;
	}

	public void setDatePrevue(LocalDate datePrevue) {
		this.datePrevue = datePrevue;
	}

	public TypeVaccin getTypeVaccin() {
		return typeVaccin;
	}

	public void setTypeVaccin(TypeVaccin typeVaccin) {
		this.typeVaccin = typeVaccin;
	}

	public String getRemarque() {
		return remarque;
	}

	public void setRemarque(String remarque) {
		this.remarque = remarque;
	}

	public Set<Classe> getClasses() {
		return classes;
	}

	public void setClasses(Set<Classe> classes) {
		this.classes = classes;
	}
}
