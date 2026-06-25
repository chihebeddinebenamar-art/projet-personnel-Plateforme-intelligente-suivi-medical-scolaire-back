package tn.educanet.pfe.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.persistence.ParentNotification;
import tn.educanet.pfe.persistence.TypeMaladieEleve;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.repository.ParentNotificationRepository;
import tn.educanet.pfe.service.ParentNotificationService;

@Service
public class ParentNotificationServiceImpl implements ParentNotificationService {

	@Resource
	private ParentNotificationRepository parentNotificationRepository;

	@Resource
	private EleveRepository eleveRepository;

	@Resource
	private JavaMailSender mailSender;

	@Value("${app.mail.from:}")
	private String mailFrom;

	@Value("${app.mail.min-interval-ms:120000}")
	private long minMailIntervalMs;

	private final Map<String, Long> lastEmailByRecipient = new ConcurrentHashMap<>();

	@Override
	@Transactional(readOnly = true)
	public List<ParentNotification> listByNumeroParent(String numeroParent) {
		String normalized = normalizePhone(numeroParent);
		if (!StringUtils.hasText(normalized)) {
			return List.of();
		}
		return parentNotificationRepository.findByNumeroParentOrderByCreatedAtDesc(normalized);
	}

	@Override
	@Transactional
	public void publishVaccinationReminder(Collection<Eleve> eleves, String typeVaccinNom, LocalDate datePrevue, String remarque) {
		if (eleves == null || eleves.isEmpty()) {
			return;
		}
		String date = datePrevue != null ? datePrevue.toString() : "date non précisée";
		String sujet = "Rappel de vaccination";
		Map<String, List<Eleve>> byPhone = new LinkedHashMap<>();
		for (Eleve eleve : eleves) {
			if (eleve == null) {
				continue;
			}
			String numero = normalizePhone(eleve.getNumeroParent());
			if (!StringUtils.hasText(numero)) {
				continue;
			}
			byPhone.computeIfAbsent(numero, k -> new ArrayList<>()).add(eleve);
			String message = "Rappel de vaccination (" + safe(typeVaccinNom) + ") prévu le " + date
					+ " pour " + fullName(eleve) + ", classe " + classeLabel(eleve) + ". Consultez l'espace parent.";
			if (StringUtils.hasText(remarque)) {
				message += " Remarque: " + remarque.trim();
			}
			saveInternal(numero, eleve, "RAPPEL_VACCINATION", sujet, message);
		}
		for (Map.Entry<String, List<Eleve>> entry : byPhone.entrySet()) {
			List<Eleve> enfants = entry.getValue();
			if (enfants.isEmpty()) {
				continue;
			}
			StringBuilder corps = new StringBuilder();
			corps.append("Rappel de vaccination (").append(safe(typeVaccinNom)).append(") prévu le ").append(date).append(".\n");
			corps.append("Enfants concernés :\n");
			for (Eleve e : enfants) {
				corps.append("- ").append(fullName(e)).append(" — classe ").append(classeLabel(e)).append("\n");
			}
			if (StringUtils.hasText(remarque)) {
				corps.append("Remarque: ").append(remarque.trim()).append("\n");
			}
			sendEmail(enfants.get(0).getEmailParent(), sujet, corps.toString());
		}
	}

	@Override
	@Transactional
	public void publishConsultation(Eleve eleve) {
		if (eleve == null) {
			return;
		}
		String numero = normalizePhone(eleve.getNumeroParent());
		if (!StringUtils.hasText(numero)) {
			return;
		}
		saveInternal(numero, eleve, "CONSULTATION", "Nouvelle consultation",
				"Une nouvelle consultation a été enregistrée pour " + fullName(eleve) + ", classe " + classeLabel(eleve) + ".");
	}

	@Override
	@Transactional
	public void publishVaccination(Eleve eleve, String typeVaccinNom) {
		if (eleve == null) {
			return;
		}
		String numero = normalizePhone(eleve.getNumeroParent());
		if (!StringUtils.hasText(numero)) {
			return;
		}
		saveInternal(numero, eleve, "VACCINATION", "Vaccination enregistrée",
				"Une vaccination (" + safe(typeVaccinNom) + ") a été enregistrée pour " + fullName(eleve)
						+ ", classe " + classeLabel(eleve) + ".");
	}

	@Override
	@Transactional
	public void publishVaccinationEnAttente(Eleve eleve, String typeVaccinNom, LocalDate datePrevue) {
		if (eleve == null) {
			return;
		}
		String numero = normalizePhone(eleve.getNumeroParent());
		if (!StringUtils.hasText(numero)) {
			return;
		}
		String titre = "Vaccination en attente";
		StringBuilder message = new StringBuilder();
		message.append("Concernant ").append(fullName(eleve)).append(", classe ").append(classeLabel(eleve))
				.append(" : la vaccination (").append(safe(typeVaccinNom)).append(") est enregistrée en attente. ");
		message.append("Votre enfant était absent lors de la séance de vaccination ou l'administration n'a pas encore été réalisée.");
		if (datePrevue != null) {
			message.append(" Date prévue : ").append(datePrevue).append(".");
		}
		message.append("Contactez l’administration afin de trouver une solution.");
		String body = message.toString();
		saveInternal(numero, eleve, "VACCINATION_EN_ATTENTE", titre, body);
		sendEmail(eleve.getEmailParent(), titre, body);
	}

	@Override
	@Transactional
	public void publishAccident(Eleve eleve, String diagnostic) {
		if (eleve == null) {
			return;
		}
		String numero = normalizePhone(eleve.getNumeroParent());
		if (!StringUtils.hasText(numero)) {
			return;
		}
		String message = "Un accident scolaire a été enregistré pour " + fullName(eleve) + ", classe " + classeLabel(eleve) + ".";
		if (StringUtils.hasText(diagnostic)) {
			message += " Diagnostic: " + diagnostic.trim();
		}
		saveInternal(numero, eleve, "ACCIDENT", "Accident scolaire", message);
		sendEmail(eleve.getEmailParent(), "Accident scolaire", message);
	}

	@Override
	@Transactional
	public void publishMaladie(Eleve eleve, TypeMaladieEleve type, String libelle) {
		if (eleve == null) {
			return;
		}
		String numero = normalizePhone(eleve.getNumeroParent());
		if (!StringUtils.hasText(numero)) {
			return;
		}
		String nature = type == TypeMaladieEleve.ALLERGIE ? "allergie" : "maladie";
		String titre = type == TypeMaladieEleve.ALLERGIE ? "Allergie déclarée" : "Maladie déclarée";
		String message = "Une " + nature + " a été enregistrée pour " + fullName(eleve) + ", classe " + classeLabel(eleve)
				+ " : " + safe(libelle) + ".";
		saveInternal(numero, eleve, type == TypeMaladieEleve.ALLERGIE ? "ALLERGIE" : "MALADIE", titre, message);
		sendEmail(eleve.getEmailParent(), titre, message);
	}

	@Override
	@Transactional
	public String publishEleveRiskElevated(Long eleveId, int riskScore, String detail) {
		if (eleveId == null) {
			return "Élève manquant.";
		}
		return eleveRepository.findById(eleveId).map(eleve -> publishEleveRiskElevated(eleve, riskScore, detail))
				.orElse("Élève introuvable.");
	}

	private String publishEleveRiskElevated(Eleve eleve, int riskScore, String detail) {
		String numero = normalizePhone(eleve.getNumeroParent());
		if (!StringUtils.hasText(numero)) {
			return "Numéro parent absent : notification non envoyée.";
		}
		LocalDateTime since = LocalDateTime.now().minusHours(24);
		if (parentNotificationRepository.existsByEleveIdAndTypeEvenementAndCreatedAtAfter(eleve.getId(), "RISQUE_ELEVE",
				since)) {
			return "Notification déjà envoyée au cours des dernières 24 h.";
		}
		String titre = "Vigilance santé — profil signalé à risque élevé";
		StringBuilder message = new StringBuilder();
		message.append("L'infirmerie signale un indicateur de risque élevé (score ").append(Math.max(0, Math.min(100, riskScore)))
				.append("/100) pour ").append(fullName(eleve)).append(", classe ").append(classeLabel(eleve))
				.append(". Il s'agit d'une aide à la décision interne, sans diagnostic médical.");
		if (StringUtils.hasText(detail)) {
			String d = detail.trim();
			if (d.length() > 800) {
				d = d.substring(0, 797) + "...";
			}
			message.append(" Éléments repérés : ").append(d).append(".");
		}
		message.append(" N'hésitez pas à contacter l'établissement pour toute question.");
		String body = message.toString();
		if (body.length() > 1950) {
			body = body.substring(0, 1947) + "...";
		}
		saveInternal(numero, eleve, "RISQUE_ELEVE", titre, body);
		sendEmail(eleve.getEmailParent(), titre, body);
		return "Notification parent enregistrée.";
	}

	private void saveInternal(String numeroParent, Eleve eleve, String type, String titre, String message) {
		ParentNotification n = new ParentNotification();
		n.setNumeroParent(numeroParent);
		if (eleve != null) {
			n.setEleveId(eleve.getId());
			n.setEleveNomComplet(fullName(eleve));
			n.setClasseNom(classeNomOnly(eleve));
		}
		n.setTypeEvenement(type);
		n.setTitre(titre);
		n.setMessage(message);
		parentNotificationRepository.save(n);
	}

	private void sendEmail(String recipient, String subject, String body) {
		if (!StringUtils.hasText(recipient) || !StringUtils.hasText(mailFrom)) {
			return;
		}
		String email = recipient.trim().toLowerCase();
		long now = System.currentTimeMillis();
		Long last = lastEmailByRecipient.get(email);
		if (last != null && (now - last) < Math.max(minMailIntervalMs, 0L)) {
			return;
		}
		try {
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setFrom(mailFrom);
			msg.setTo(email);
			msg.setSubject(subject);
			msg.setText(body);
			mailSender.send(msg);
			lastEmailByRecipient.put(email, now);
		} catch (Exception ignored) {
		}
	}

	private static String normalizePhone(String s) {
		if (!StringUtils.hasText(s)) {
			return "";
		}
		return s.replaceAll("\\s+", "").trim();
	}

	private static String fullName(Eleve e) {
		String nom = e.getNom() != null ? e.getNom().trim() : "";
		String prenom = e.getPrenom() != null ? e.getPrenom().trim() : "";
		return (nom + " " + prenom).trim();
	}

	private static String classeNomOnly(Eleve e) {
		if (e == null || e.getClasse() == null || !StringUtils.hasText(e.getClasse().getNom())) {
			return null;
		}
		return e.getClasse().getNom().trim();
	}

	private static String classeLabel(Eleve e) {
		String n = classeNomOnly(e);
		return StringUtils.hasText(n) ? n : "non renseignée";
	}

	private static String safe(String s) {
		return StringUtils.hasText(s) ? s.trim() : "non précisé";
	}
}
