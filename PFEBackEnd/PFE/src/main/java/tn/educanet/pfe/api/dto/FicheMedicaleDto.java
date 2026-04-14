package tn.educanet.pfe.api.dto;

import java.util.List;

public class FicheMedicaleDto {

	private EleveDto eleve;
	private List<VaccinationDto> vaccinations;
	private List<ConsultationDto> consultations;
	private List<AccidentDto> accidents;
	/** Allergies et maladies chroniques déclarées pour l’élève. */
	private List<MaladieEleveDto> maladies;
	/** Une photo de carnet numérique peut être associée (GET …/carnet-numerique/image). */
	private boolean carnetNumeriquePresent;
	/** Pour invalider le cache navigateur après remplacement de l’image (epoch ms). */
	private Long carnetNumeriqueVersion;

	public FicheMedicaleDto() {
	}

	public FicheMedicaleDto(EleveDto eleve, List<VaccinationDto> vaccinations, List<ConsultationDto> consultations,
			List<AccidentDto> accidents, List<MaladieEleveDto> maladies, boolean carnetNumeriquePresent,
			Long carnetNumeriqueVersion) {
		this.eleve = eleve;
		this.vaccinations = vaccinations;
		this.consultations = consultations;
		this.accidents = accidents;
		this.maladies = maladies;
		this.carnetNumeriquePresent = carnetNumeriquePresent;
		this.carnetNumeriqueVersion = carnetNumeriqueVersion;
	}

	public EleveDto getEleve() {
		return eleve;
	}

	public void setEleve(EleveDto eleve) {
		this.eleve = eleve;
	}

	public List<VaccinationDto> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(List<VaccinationDto> vaccinations) {
		this.vaccinations = vaccinations;
	}

	public List<ConsultationDto> getConsultations() {
		return consultations;
	}

	public void setConsultations(List<ConsultationDto> consultations) {
		this.consultations = consultations;
	}

	public List<AccidentDto> getAccidents() {
		return accidents;
	}

	public void setAccidents(List<AccidentDto> accidents) {
		this.accidents = accidents;
	}

	public List<MaladieEleveDto> getMaladies() {
		return maladies;
	}

	public void setMaladies(List<MaladieEleveDto> maladies) {
		this.maladies = maladies;
	}

	public boolean isCarnetNumeriquePresent() {
		return carnetNumeriquePresent;
	}

	public void setCarnetNumeriquePresent(boolean carnetNumeriquePresent) {
		this.carnetNumeriquePresent = carnetNumeriquePresent;
	}

	public Long getCarnetNumeriqueVersion() {
		return carnetNumeriqueVersion;
	}

	public void setCarnetNumeriqueVersion(Long carnetNumeriqueVersion) {
		this.carnetNumeriqueVersion = carnetNumeriqueVersion;
	}
}
