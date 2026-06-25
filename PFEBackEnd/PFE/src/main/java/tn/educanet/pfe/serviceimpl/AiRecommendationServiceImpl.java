package tn.educanet.pfe.serviceimpl;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import tn.educanet.pfe.persistence.Consultation;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.persistence.EleveMaladie;
import tn.educanet.pfe.persistence.TypeMaladieEleve;
import tn.educanet.pfe.persistence.Vaccination;
import tn.educanet.pfe.repository.ConsultationRepository;
import tn.educanet.pfe.repository.EleveMaladieRepository;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.repository.VaccinationRepository;
import tn.educanet.pfe.service.AiRecommendationResult;
import tn.educanet.pfe.service.AiRecommendationService;

@Service
public class AiRecommendationServiceImpl implements AiRecommendationService {

	private static final String DISCLAIMER = "Conseils IA non critiques: validation médicale humaine obligatoire.";

	private final EleveRepository eleveRepository;
	private final VaccinationRepository vaccinationRepository;
	private final ConsultationRepository consultationRepository;
	private final EleveMaladieRepository eleveMaladieRepository;
	private final RestTemplate restTemplate;

	@Value("${app.ai.ollama.url:http://localhost:11434/api/generate}")
	private String ollamaUrl;

	@Value("${app.ai.ollama.model:llama3.1}")
	private String ollamaModel;

	@Value("${app.ai.ollama.enabled:true}")
	private boolean ollamaEnabled;

	public AiRecommendationServiceImpl(
			EleveRepository eleveRepository,
			VaccinationRepository vaccinationRepository,
			ConsultationRepository consultationRepository,
			EleveMaladieRepository eleveMaladieRepository,
			RestTemplateBuilder restTemplateBuilder) {
		this.eleveRepository = eleveRepository;
		this.vaccinationRepository = vaccinationRepository;
		this.consultationRepository = consultationRepository;
		this.eleveMaladieRepository = eleveMaladieRepository;
		this.restTemplate = restTemplateBuilder.build();
	}

	@Override
	public AiRecommendationResult generateForEleve(Long eleveId) {
		if (eleveId == null) {
			return AiRecommendationResult.fail("Identifiant élève manquant.");
		}
		Eleve eleve = eleveRepository.findById(eleveId).orElse(null);
		if (eleve == null) {
			return AiRecommendationResult.fail("Élève introuvable.");
		}

		List<Vaccination> vaccinations = vaccinationRepository.findByEleveIdOrderByDateVaccinationDesc(eleveId);
		List<Consultation> consultations = consultationRepository.findByEleveIdOrderByDateConsultationDesc(eleveId);
		List<EleveMaladie> maladies = eleveMaladieRepository.findByEleveIdOrderByTypeAscLibelleAsc(eleveId);
		List<Eleve> allEleves = eleveRepository.findAll();
		List<Vaccination> allVaccinations = vaccinationRepository.findAll();
		List<EleveMaladie> allMaladies = eleveMaladieRepository.findAll();

		CleanPatientData cleaned = cleanPatientData(eleve, vaccinations, consultations, maladies);
		StatsSnapshot stats = computeStats(allEleves, allVaccinations, allMaladies);
		List<String> rules = applyBusinessRules(cleaned);
		String patientSummary = buildPatientSummary(cleaned);
		String statsSummary = buildStatsSummary(stats);
		String rulesSummary = String.join("\n- ", prependBulletSeed(rules));
		String prompt = buildPrompt(patientSummary, statsSummary, rulesSummary);

		LlmSections llm = ollamaEnabled ? callOllama(prompt) : null;
		if (llm == null) {
			llm = fallbackSections(cleaned, rules);
		}

		return new AiRecommendationResult(
				true,
				DISCLAIMER,
				patientSummary,
				statsSummary,
				rulesSummary,
				prompt,
				llm.recommendations(),
				llm.vaccinationPlan(),
				llm.preventionAdvice());
	}

	private CleanPatientData cleanPatientData(
			Eleve eleve,
			List<Vaccination> vaccinations,
			List<Consultation> consultations,
			List<EleveMaladie> maladies) {
		String fullName = normalizeText(eleve.getPrenom()) + " " + normalizeText(eleve.getNom());
		String sexe = normalizeText(eleve.getSexe()).toUpperCase(Locale.ROOT);
		if (!StringUtils.hasText(sexe)) {
			sexe = "NON_PRECISE";
		}
		int age = eleve.getDateNaissance() != null ? Math.max(0, Period.between(eleve.getDateNaissance(), LocalDate.now()).getYears()) : -1;

		List<Vaccination> sortedVaccinations = vaccinations.stream()
				.filter(Objects::nonNull)
				.filter(v -> v.getDateVaccination() != null || v.getDatePrevue() != null)
				.sorted(Comparator.comparing((Vaccination v) -> safeDate(v.getDateVaccination(), v.getDatePrevue())).reversed())
				.toList();

		List<String> maladieLabels = maladies.stream()
				.filter(Objects::nonNull)
				.map(EleveMaladie::getLibelle)
				.map(this::normalizeText)
				.filter(StringUtils::hasText)
				.distinct()
				.toList();

		boolean hasChronic = maladies.stream().anyMatch(m -> m != null && m.getType() == TypeMaladieEleve.MALADIE_CHRONIQUE);
		boolean hasAllergy = maladies.stream().anyMatch(m -> m != null && m.getType() == TypeMaladieEleve.ALLERGIE);
		boolean feverRecent = consultations.stream()
				.filter(Objects::nonNull)
				.filter(c -> c.getDateConsultation() != null)
				.filter(c -> !c.getDateConsultation().isBefore(LocalDate.now().minusDays(30)))
				.anyMatch(c -> c.getTemperature() != null && c.getTemperature() >= 38d);

		return new CleanPatientData(fullName.trim(), age, sexe, sortedVaccinations, maladieLabels, hasChronic, hasAllergy, feverRecent);
	}

	private StatsSnapshot computeStats(List<Eleve> allEleves, List<Vaccination> allVaccinations, List<EleveMaladie> allMaladies) {
		int totalEleves = allEleves.size();
		int totalVaccinations = allVaccinations.size();
		long elevesVaccines = allVaccinations.stream()
				.filter(v -> v.getEleve() != null && v.getEleve().getId() != null)
				.map(v -> v.getEleve().getId())
				.distinct()
				.count();
		double vaccinationRate = totalEleves == 0 ? 0d : (elevesVaccines * 100d) / totalEleves;
		long elevesNonVaccines = Math.max(0L, totalEleves - elevesVaccines);
		long vaccinations30j = allVaccinations.stream()
				.filter(v -> v.getDateVaccination() != null)
				.filter(v -> !v.getDateVaccination().isBefore(LocalDate.now().minusDays(30)))
				.count();
		long dosesEnAttente = allVaccinations.stream()
				.filter(v -> "EN_ATTENTE".equalsIgnoreCase(normalizeText(v.getStatus())))
				.count();
		long age0to5 = allEleves.stream().filter(e -> ageBetween(e, 0, 5)).count();
		long age6to11 = allEleves.stream().filter(e -> ageBetween(e, 6, 11)).count();
		long age12plus = allEleves.stream().filter(e -> ageBetween(e, 12, 120)).count();

		Map<String, Long> diseaseDistribution = allMaladies.stream()
				.map(EleveMaladie::getLibelle)
				.map(this::normalizeText)
				.filter(StringUtils::hasText)
				.collect(Collectors.groupingBy(s -> s, LinkedHashMap::new, Collectors.counting()));

		long riskChronic = allMaladies.stream().filter(m -> m.getType() == TypeMaladieEleve.MALADIE_CHRONIQUE).count();
		long riskAllergy = allMaladies.stream().filter(m -> m.getType() == TypeMaladieEleve.ALLERGIE).count();

		return new StatsSnapshot(
				totalEleves,
				totalVaccinations,
				vaccinationRate,
				elevesVaccines,
				elevesNonVaccines,
				vaccinations30j,
				dosesEnAttente,
				age0to5,
				age6to11,
				age12plus,
				diseaseDistribution,
				riskChronic,
				riskAllergy);
	}

	private List<String> applyBusinessRules(CleanPatientData cleaned) {
		List<String> rules = new ArrayList<>();
		if (cleaned.age() >= 0 && cleaned.age() <= 6) {
			rules.add("Âge 0-6 ans: prioriser rappels du calendrier vaccinal pédiatrique.");
		} else if (cleaned.age() <= 12) {
			rules.add("Âge 7-12 ans: vérifier rappels DTP et vaccins scolaires obligatoires.");
		} else {
			rules.add("Âge > 12 ans: vérifier rappels adolescents et statut vaccinal complet.");
		}
		if (cleaned.vaccinations().isEmpty()) {
			rules.add("Aucune dose enregistrée: démarrer un plan de rattrapage vaccinal.");
		}
		LocalDate lastDate = cleaned.vaccinations().stream()
				.map(Vaccination::getDateVaccination)
				.filter(Objects::nonNull)
				.max(LocalDate::compareTo)
				.orElse(null);
		if (lastDate != null && lastDate.isAfter(LocalDate.now().minusDays(30))) {
			rules.add("Dernière vaccination récente (<30j): éviter un rappel trop rapproché.");
		}
		if (cleaned.hasChronic()) {
			rules.add("Maladie chronique: renforcer le suivi et valider les contre-indications avant injection.");
		}
		if (cleaned.hasAllergy()) {
			rules.add("Allergie connue: contrôle strict des antécédents allergiques avant vaccination.");
		}
		if (cleaned.feverRecent()) {
			rules.add("Fièvre récente: reporter les injections non urgentes jusqu'à stabilisation clinique.");
		}
		return rules;
	}

	private String buildPrompt(String patientSummary, String statsSummary, String rulesSummary) {
		return """
				Tu es un assistant de soutien médical scolaire.
				Tu dois fournir des recommandations NON CRITIQUES, prudentes, et toujours proposer validation humaine.
				Réponds en français avec 3 sections exactes:
				1) RECOMMANDATIONS
				2) PLANNING_VACCINATION
				3) PREVENTION
				
				Donnees patient:
				%s
				
				Analyse statistique:
				%s
				
				Regles metier:
				%s
				
				Contrainte securite:
				Ne jamais faire de diagnostic vital. Mentionner "validation médicale requise".
				""".formatted(patientSummary, statsSummary, rulesSummary);
	}

	private String buildPatientSummary(CleanPatientData cleaned) {
		String vaccins = cleaned.vaccinations().stream()
				.limit(8)
				.map(v -> {
					String type = v.getTypeVaccin() != null ? normalizeText(v.getTypeVaccin().getNom()) : "Vaccin non précisé";
					String date = v.getDateVaccination() != null ? v.getDateVaccination().toString() : "date inconnue";
					return type + " (dose " + v.getDose() + ", " + date + ")";
				})
				.collect(Collectors.joining(", "));
		if (!StringUtils.hasText(vaccins)) {
			vaccins = "aucune dose enregistrée";
		}
		String maladies = cleaned.maladieLabels().isEmpty() ? "aucune" : String.join(", ", cleaned.maladieLabels());
		return "Nom: %s | Age: %s | Sexe: %s | Vaccins: %s | Maladies/allergies: %s | Risques: chronique=%s, allergie=%s, fievreRecente=%s"
				.formatted(
						cleaned.fullName(),
						cleaned.age() >= 0 ? Integer.toString(cleaned.age()) : "non renseigné",
						cleaned.sexe(),
						vaccins,
						maladies,
						cleaned.hasChronic(),
						cleaned.hasAllergy(),
						cleaned.feverRecent());
	}

	private String buildStatsSummary(StatsSnapshot stats) {
		String dist = stats.diseaseDistribution().entrySet().stream()
				.limit(10)
				.map(e -> e.getKey() + "=" + e.getValue())
				.collect(Collectors.joining(", "));
		if (!StringUtils.hasText(dist)) {
			dist = "aucune maladie recensée";
		}
		return "Eleves=%d | Vaccinations=%d | ElevesVaccines=%d | ElevesNonVaccines=%d | TauxVaccination=%.1f%% | DosesEnAttente=%d | Vaccinations30j=%d | RepartitionAge(0-5=%d, 6-11=%d, 12+=%d) | DistributionMaladies={%s} | GroupesRisque(chronique=%d, allergie=%d)"
				.formatted(
						stats.totalEleves(),
						stats.totalVaccinations(),
						stats.elevesVaccines(),
						stats.elevesNonVaccines(),
						stats.vaccinationRate(),
						stats.dosesEnAttente(),
						stats.vaccinations30j(),
						stats.age0to5(),
						stats.age6to11(),
						stats.age12plus(),
						dist,
						stats.riskChronic(),
						stats.riskAllergy());
	}
	private boolean ageBetween(Eleve eleve, int min, int max) {
		if (eleve == null || eleve.getDateNaissance() == null) {
			return false;
		}
		int age = Math.max(0, Period.between(eleve.getDateNaissance(), LocalDate.now()).getYears());
		return age >= min && age <= max;
	}


	private LlmSections callOllama(String prompt) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			Map<String, Object> payload = new LinkedHashMap<>();
			payload.put("model", ollamaModel);
			payload.put("prompt", prompt);
			payload.put("stream", false);
			HttpEntity<Map<String, Object>> req = new HttpEntity<>(payload, headers);
			@SuppressWarnings("unchecked")
			Map<String, Object> response = restTemplate.postForObject(ollamaUrl, req, Map.class);
			String raw = response != null ? normalizeText((String) response.get("response")) : "";
			if (!StringUtils.hasText(raw)) {
				return null;
			}
			return parseSections(raw);
		} catch (Exception ex) {
			return null;
		}
	}

	private LlmSections parseSections(String raw) {
		String rec = section(raw, "RECOMMANDATIONS", "PLANNING_VACCINATION");
		String plan = section(raw, "PLANNING_VACCINATION", "PREVENTION");
		String prev = section(raw, "PREVENTION", null);
		if (!StringUtils.hasText(rec) && !StringUtils.hasText(plan) && !StringUtils.hasText(prev)) {
			return null;
		}
		return new LlmSections(
				StringUtils.hasText(rec) ? rec : "Validation médicale requise avant toute décision.",
				StringUtils.hasText(plan) ? plan : "Planification à confirmer selon dossier clinique.",
				StringUtils.hasText(prev) ? prev : "Conseils d'hygiène et suivi régulier recommandés.");
	}

	private LlmSections fallbackSections(CleanPatientData cleaned, List<String> rules) {
		LocalDate nextDate = cleaned.vaccinations().stream()
				.map(Vaccination::getDateVaccination)
				.filter(Objects::nonNull)
				.max(LocalDate::compareTo)
				.map(d -> d.plusDays(30))
				.orElse(LocalDate.now().plusDays(7));
		String rec = "Prioriser le suivi vaccinal selon l'âge et les antécédents. " + DISCLAIMER;
		if (!rules.isEmpty()) {
			rec += " Règles clés: " + String.join(" | ", rules);
		}
		String plan = "Date optimale suggérée pour revue vaccinale: " + nextDate + ". Confirmer avec le médecin scolaire.";
		String prev = "Surveiller symptômes post-vaccin, maintenir hygiène, et consulter en cas de fièvre persistante.";
		return new LlmSections(rec, plan, prev);
	}

	private String section(String raw, String start, String next) {
		String upper = raw.toUpperCase(Locale.ROOT);
		int s = upper.indexOf(start);
		if (s < 0) {
			return "";
		}
		int contentStart = s + start.length();
		int e = next != null ? upper.indexOf(next, contentStart) : -1;
		String part = e >= 0 ? raw.substring(contentStart, e) : raw.substring(contentStart);
		return normalizeText(part.replaceFirst("^[:\\-\\s]+", ""));
	}

	private LocalDate safeDate(LocalDate first, LocalDate fallback) {
		return first != null ? first : fallback;
	}

	private String normalizeText(String value) {
		if (!StringUtils.hasText(value)) {
			return "";
		}
		return value.replaceAll("\\s+", " ").trim();
	}

	private List<String> prependBulletSeed(List<String> lines) {
		List<String> result = new ArrayList<>();
		result.add("Règles appliquées");
		result.addAll(lines);
		return result;
	}

	private record CleanPatientData(
			String fullName,
			int age,
			String sexe,
			List<Vaccination> vaccinations,
			List<String> maladieLabels,
			boolean hasChronic,
			boolean hasAllergy,
			boolean feverRecent) {
	}

	private record StatsSnapshot(
			int totalEleves,
			int totalVaccinations,
			double vaccinationRate,
			long elevesVaccines,
			long elevesNonVaccines,
			long vaccinations30j,
			long dosesEnAttente,
			long age0to5,
			long age6to11,
			long age12plus,
			Map<String, Long> diseaseDistribution,
			long riskChronic,
			long riskAllergy) {
	}

	private record LlmSections(String recommendations, String vaccinationPlan, String preventionAdvice) {
	}
}
