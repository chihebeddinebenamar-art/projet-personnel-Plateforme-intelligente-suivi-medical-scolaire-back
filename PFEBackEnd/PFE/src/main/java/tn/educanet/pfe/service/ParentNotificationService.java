package tn.educanet.pfe.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.persistence.ParentNotification;
import tn.educanet.pfe.persistence.TypeMaladieEleve;

public interface ParentNotificationService {

	List<ParentNotification> listByNumeroParent(String numeroParent);

	void publishVaccinationReminder(Collection<Eleve> eleves, String typeVaccinNom, LocalDate datePrevue, String remarque);

	void publishConsultation(Eleve eleve);

	void publishVaccination(Eleve eleve, String typeVaccinNom);

	/**
	 * Vaccination enregistrée en attente (élève absent à la séance ou administration non réalisée).
	 */
	void publishVaccinationEnAttente(Eleve eleve, String typeVaccinNom, LocalDate datePrevue);

	void publishAccident(Eleve eleve, String diagnostic);

	void publishMaladie(Eleve eleve, TypeMaladieEleve type, String libelle);

	/**
	 * Notification espace parent + e-mail (si configuré) lorsque l'infirmerie signale un risque élevé.
	 * Idempotent : pas de nouvelle notification si une alerte {@code RISQUE_ELEVE} existe déjà pour cet élève
	 * dans les dernières 24 h.
	 *
	 * @return message court pour accusé de réception SOAP (créée, ignorée doublon, élève introuvable, etc.)
	 */
	String publishEleveRiskElevated(Long eleveId, int riskScore, String detail);
}
