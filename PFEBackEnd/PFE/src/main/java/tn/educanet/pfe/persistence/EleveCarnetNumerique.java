package tn.educanet.pfe.persistence;

import java.io.Serializable;
import java.time.Instant;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "eleve_carnet_numerique")
public class EleveCarnetNumerique implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "eleve_id", nullable = false, unique = true)
	private Eleve eleve;

	/** Ancien stockage image unique (nullable si photos dans {@code EleveCarnetNumeriquePhoto}). */
	@Column(name = "stored_filename", length = 255)
	private String storedFilename;

	@Column(name = "content_type", length = 100)
	private String contentType;

	@Column(name = "description", length = 2000)
	private String description;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	public EleveCarnetNumerique() {
	}

	public EleveCarnetNumerique(Long id, Eleve eleve, String storedFilename, String contentType, Instant updatedAt) {
		this.id = id;
		this.eleve = eleve;
		this.storedFilename = storedFilename;
		this.contentType = contentType;
		this.updatedAt = updatedAt;
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

	public String getStoredFilename() {
		return storedFilename;
	}

	public void setStoredFilename(String storedFilename) {
		this.storedFilename = storedFilename;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}
