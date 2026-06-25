package tn.educanet.pfe.serviceimpl;

import java.util.List;
import java.time.LocalDate;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dozer.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import jakarta.annotation.Resource;

import com.tn.educanet.pfe.api.eleves.schema.EleveDto;
import com.tn.educanet.pfe.api.eleves.schema.EleveRequest;
import com.tn.educanet.pfe.api.eleves.schema.FicheMedicaleDto;

import tn.educanet.pfe.exception.BusinessException;
import tn.educanet.pfe.persistence.Classe;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.repository.AccidentRepository;
import tn.educanet.pfe.repository.ClasseRepository;
import tn.educanet.pfe.repository.ConsultationRepository;
import tn.educanet.pfe.repository.EleveCarnetNumeriqueRepository;
import tn.educanet.pfe.repository.EleveMaladieRepository;
import tn.educanet.pfe.repository.EleveRepository;
import tn.educanet.pfe.repository.VaccinationRepository;
import tn.educanet.pfe.service.ChangeParentPasswordResult;
import tn.educanet.pfe.service.EleveCarnetNumeriqueService;
import tn.educanet.pfe.service.EleveService;
import tn.educanet.pfe.specification.EleveSpecification;
import tn.educanet.pfe.util.SchemaDateUtils;

@Service
public class EleveServiceImpl implements EleveService {

	@Resource
	private EleveRepository eleveRepository;

	@Resource
	private ClasseRepository classeRepository;

	@Resource
	private VaccinationRepository vaccinationRepository;

	@Resource
	private ConsultationRepository consultationRepository;

	@Resource
	private AccidentRepository accidentRepository;

	@Resource
	private EleveMaladieRepository eleveMaladieRepository;

	@Resource
	private EleveCarnetNumeriqueRepository eleveCarnetNumeriqueRepository;

	@Resource
	private EleveCarnetNumeriqueService eleveCarnetNumeriqueService;

	@Resource
	private tn.educanet.pfe.service.ElevePhotoProfilService elevePhotoProfilService;
	@Resource
	private Mapper mapper;

	@Resource
	private PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public List<EleveDto> listerFiltres(Long niveauId, Long classeId, String recherche) {
		return eleveRepository.findAll(EleveSpecification.filtres(niveauId, classeId, recherche)).stream()
				.map(this::mapEleveDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<EleveDto> parClasse(Long classeId) {
		return eleveRepository.findByClasseIdOrderByNomAscPrenomAsc(classeId).stream().map(this::mapEleveDto)
				.toList();
	}

	@Override
	@Transactional
	public EleveDto creer(EleveRequest request) {
		Classe classe = classeRepository.findById(request.getClasseId())
				.orElseThrow(() -> new BusinessException("Classe introuvable"));
		String mat = request.getMatricule() != null ? request.getMatricule().trim() : null;
		if (mat != null && !mat.isEmpty() && eleveRepository.existsByMatricule(mat)) {
			throw new BusinessException("Ce matricule existe déjà : " + mat);
		}
		Eleve e = new Eleve();
		e.setMatricule(mat);
		e.setNom(request.getNom());
		e.setPrenom(request.getPrenom());
		LocalDate dateNaissance = SchemaDateUtils.toLocalDate(request.getDateNaissance());
		validateDateNaissance(dateNaissance);
		e.setDateNaissance(dateNaissance);
		e.setNumeroParent(request.getNumeroParent());
		e.setEmailParent(readOptionalField(request, "getEmailParent", "emailParent", "email_parent", "parentEmail"));
		e.setSexe(readOptionalField(request, "getSexe", "sexe", "sex", "gender"));
		e.setClasse(classe);
		applyParentPasswordRules(e, request, true);
		return mapEleveDto(eleveRepository.save(e));
	}

	@Override
	@Transactional
	public EleveDto modifier(Long id, EleveRequest request) {
		Eleve e = eleveRepository.findById(id).orElseThrow(() -> new BusinessException("Élève introuvable"));
		String previousNumeroParent = e.getNumeroParent();
		Classe classe = classeRepository.findById(request.getClasseId())
				.orElseThrow(() -> new BusinessException("Classe introuvable"));
		String mat = request.getMatricule() != null ? request.getMatricule().trim() : null;
		if (mat != null && !mat.isEmpty() && eleveRepository.existsByMatriculeAndIdNot(mat, id)) {
			throw new BusinessException("Ce matricule est déjà utilisé par un autre élève : " + mat);
		}
		e.setMatricule(mat);
		e.setNom(request.getNom());
		e.setPrenom(request.getPrenom());
		LocalDate dateNaissance = SchemaDateUtils.toLocalDate(request.getDateNaissance());
		validateDateNaissance(dateNaissance);
		e.setDateNaissance(dateNaissance);
		e.setNumeroParent(request.getNumeroParent());
		e.setEmailParent(readOptionalField(request, "getEmailParent", "emailParent", "email_parent", "parentEmail"));
		e.setSexe(readOptionalField(request, "getSexe", "sexe", "sex", "gender"));
		e.setClasse(classe);
		applyParentPasswordRules(e, request, false, previousNumeroParent);
		return mapEleveDto(eleveRepository.save(e));
	}

	@Override
	@Transactional
	public void supprimer(Long id) {
		if (!eleveRepository.existsById(id)) {
			throw new BusinessException("Élève introuvable");
		}
		eleveCarnetNumeriqueService.supprimerSiPresent(id);
		elevePhotoProfilService.supprimerSiPresent(id);
		eleveRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public EleveDto get(Long id) {
		Eleve e = eleveRepository.findById(id).orElseThrow(() -> new BusinessException("Élève introuvable"));
		return mapEleveDto(e);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean verifierCredentialsParent(String numeroTelephone, String motDePassePlain) {
		if (!StringUtils.hasText(numeroTelephone) || !StringUtils.hasText(motDePassePlain)) {
			return false;
		}
		String n = normalizeNumeroParent(numeroTelephone);
		return eleveRepository.findAll().stream()
				.filter(e -> StringUtils.hasText(e.getNumeroParent()))
				.filter(e -> n.equals(normalizeNumeroParent(e.getNumeroParent())))
				.anyMatch(e -> StringUtils.hasText(e.getPasswordParent())
						&& passwordEncoder.matches(motDePassePlain, e.getPasswordParent()));
	}

	@Override
	@Transactional
	public ChangeParentPasswordResult changerMotDePasseParent(String numeroTelephone, String ancienPlain,
			String nouveauPlain) {
		if (!StringUtils.hasText(nouveauPlain) || nouveauPlain.trim().length() < 6) {
			return ChangeParentPasswordResult.fail("Le nouveau mot de passe doit contenir au moins 6 caractères.");
		}
		if (!verifierCredentialsParent(numeroTelephone, ancienPlain)) {
			return ChangeParentPasswordResult.fail("Mot de passe actuel incorrect.");
		}
		String n = normalizeNumeroParent(numeroTelephone);
		List<Eleve> list = eleveRepository.findAll().stream()
				.filter(e -> StringUtils.hasText(e.getNumeroParent()))
				.filter(e -> n.equals(normalizeNumeroParent(e.getNumeroParent())))
				.toList();
		if (list.isEmpty()) {
			return ChangeParentPasswordResult.fail("Aucun compte élève associé à ce numéro.");
		}
		String hash = passwordEncoder.encode(nouveauPlain.trim());
		for (Eleve e : list) {
			e.setPasswordParent(hash);
		}
		eleveRepository.saveAll(list);
		return ChangeParentPasswordResult.success();
	}

	@Override
	@Transactional(readOnly = true)
	public FicheMedicaleDto ficheMedicale(Long eleveId) {
		Eleve e = eleveRepository.findById(eleveId).orElseThrow(() -> new BusinessException("Élève introuvable"));
		var vac = vaccinationRepository.findByEleveIdOrderByDateVaccinationDesc(eleveId);
		var cons = consultationRepository.findByEleveIdOrderByDateConsultationDesc(eleveId);
		var acc = accidentRepository.findByEleveIdOrderByDateAccidentDesc(eleveId);
		var mal = eleveMaladieRepository.findByEleveIdOrderByTypeAscLibelleAsc(eleveId);
		var carnetOpt = eleveCarnetNumeriqueRepository.findByEleveId(eleveId);
		boolean carnetPresent = eleveCarnetNumeriqueService.hasImage(eleveId);
		Long carnetVer = carnetOpt.map(c -> c.getUpdatedAt().toEpochMilli()).orElse(null);
		FicheMedicaleDto fm = new FicheMedicaleDto();
		fm.setEleve(mapEleveDto(e));
		if (!vac.isEmpty()) {
			FicheMedicaleDto.Vaccinations holder = new FicheMedicaleDto.Vaccinations();
			vac.forEach(v -> holder.getItem().add(mapVaccinationForFiche(v)));
			fm.setVaccinations(holder);
		}
		if (!cons.isEmpty()) {
			FicheMedicaleDto.Consultations holder = new FicheMedicaleDto.Consultations();
			cons.forEach(c -> holder.getItem().add(mapConsultationForFiche(c)));
			fm.setConsultations(holder);
		}
		if (!acc.isEmpty()) {
			FicheMedicaleDto.Accidents holder = new FicheMedicaleDto.Accidents();
			acc.forEach(a -> holder.getItem().add(mapAccidentForFiche(a)));
			fm.setAccidents(holder);
		}
		if (!mal.isEmpty()) {
			FicheMedicaleDto.Maladies holder = new FicheMedicaleDto.Maladies();
			mal.forEach(m -> holder.getItem().add(mapMaladieForFiche(m)));
			fm.setMaladies(holder);
		}
		fm.setCarnetNumeriquePresent(carnetPresent);
		fm.setCarnetNumeriqueVersion(carnetVer);
		return fm;
	}

	private EleveDto mapEleveDto(Eleve e) {
		EleveDto dto = mapper.map(e, EleveDto.class);
		setOptionalOnDto(dto, "setEmailParent", e.getEmailParent(), "emailParent");
		setOptionalOnDto(dto, "setSexe", e.getSexe(), "sexe");
		return dto;
	}

	private com.tn.educanet.pfe.api.eleves.schema.VaccinationDto mapVaccinationForFiche(
			tn.educanet.pfe.persistence.Vaccination v) {
		com.tn.educanet.pfe.api.eleves.schema.VaccinationDto dto = mapper.map(v,
				com.tn.educanet.pfe.api.eleves.schema.VaccinationDto.class);
		dto.setEleveNomComplet(nomComplet(v.getEleve()));
		return dto;
	}

	private com.tn.educanet.pfe.api.eleves.schema.ConsultationDto mapConsultationForFiche(
			tn.educanet.pfe.persistence.Consultation c) {
		com.tn.educanet.pfe.api.eleves.schema.ConsultationDto dto = mapper.map(c,
				com.tn.educanet.pfe.api.eleves.schema.ConsultationDto.class);
		dto.setEleveNomComplet(nomComplet(c.getEleve()));
		return dto;
	}

	private com.tn.educanet.pfe.api.eleves.schema.AccidentDto mapAccidentForFiche(
			tn.educanet.pfe.persistence.Accident a) {
		com.tn.educanet.pfe.api.eleves.schema.AccidentDto dto = mapper.map(a,
				com.tn.educanet.pfe.api.eleves.schema.AccidentDto.class);
		dto.setEleveNomComplet(nomComplet(a.getEleve()));
		return dto;
	}

	private com.tn.educanet.pfe.api.eleves.schema.MaladieEleveDto mapMaladieForFiche(
			tn.educanet.pfe.persistence.EleveMaladie m) {
		return mapper.map(m, com.tn.educanet.pfe.api.eleves.schema.MaladieEleveDto.class);
	}

	private String nomComplet(Eleve e) {
		if (e == null) {
			return "";
		}
		String nom = e.getNom() != null ? e.getNom() : "";
		String prenom = e.getPrenom() != null ? e.getPrenom() : "";
		return (nom + " " + prenom).trim();
	}

	private String readOptionalField(EleveRequest request, String getterName, String... anyNames) {
		if (request == null) {
			return null;
		}
		try {
			var method = request.getClass().getMethod(getterName);
			Object value = method.invoke(request);
			if (value instanceof String s && StringUtils.hasText(s)) {
				return s;
			}
		} catch (Exception ignored) {
			// JAXB class not regenerated yet: fallback to xsd:any payload
		}
		return firstAnyString(request.getAny(), anyNames);
	}

	private String firstAnyString(List<Object> any, String... names) {
		if (any == null || any.isEmpty() || names == null || names.length == 0) {
			return null;
		}
		for (Object item : any) {
			if (item instanceof Element element) {
				String local = element.getLocalName();
				String node = element.getNodeName();
				for (String name : names) {
					if (name.equals(local) || name.equals(node)) {
						String value = element.getTextContent();
						if (StringUtils.hasText(value)) {
							return value;
						}
					}
				}
			}
		}
		return null;
	}

	private void setOptionalOnDto(EleveDto dto, String setterName, String value, String anyName) {
		if (!StringUtils.hasText(value) || dto == null) {
			return;
		}
		try {
			var setter = dto.getClass().getMethod(setterName, String.class);
			setter.invoke(dto, value);
			return;
		} catch (Exception ignored) {
			// JAXB class not regenerated yet: fallback to xsd:any payload
		}
		try {
			var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			var el = doc.createElement(anyName);
			el.setTextContent(value);
			dto.getAny().add(el);
		} catch (Exception ignored) {
		}
	}

	private void applyParentPasswordRules(Eleve e, EleveRequest request, boolean create) {
		applyParentPasswordRules(e, request, create, null);
	}

	private void applyParentPasswordRules(Eleve e, EleveRequest request, boolean create, String previousNumeroParent) {
		String normalizedNumeroParent = normalizeNumeroParent(e.getNumeroParent());
		String plain = readOptionalField(request, "getPasswordParent", "passwordParent", "password_parent",
				"parentPassword");
		boolean hasNewPassword = StringUtils.hasText(plain);

		if (!StringUtils.hasText(normalizedNumeroParent)) {
			// Aucun numéro parent => pas de règles de mot de passe parent.
			return;
		}
		validateNumeroParentFormat(normalizedNumeroParent);

		Eleve existingParentAccount = findExistingParentAccountByNumero(normalizedNumeroParent, e.getId());
		if (existingParentAccount != null) {
			if (hasNewPassword) {
				throw new BusinessException(
						"Ce numéro parent existe déjà. Vous ne pouvez pas définir un nouveau mot de passe.");
			}
			if (StringUtils.hasText(existingParentAccount.getPasswordParent())) {
				e.setPasswordParent(existingParentAccount.getPasswordParent());
			}
			return;
		}

		boolean numeroChanged = create
				|| !normalizedNumeroParent.equals(normalizeNumeroParent(previousNumeroParent));
		if (numeroChanged && !hasNewPassword) {
			throw new BusinessException(
					"Nouveau numéro parent détecté : le mot de passe parent est obligatoire.");
		}
		if (hasNewPassword) {
			e.setPasswordParent(passwordEncoder.encode(plain.trim()));
		}
	}

	private Eleve findExistingParentAccountByNumero(String normalizedNumeroParent, Long excludeId) {
		if (!StringUtils.hasText(normalizedNumeroParent)) {
			return null;
		}
		return eleveRepository.findAll().stream()
				.filter(other -> other != null)
				.filter(other -> excludeId == null || !excludeId.equals(other.getId()))
				.filter(other -> StringUtils.hasText(other.getNumeroParent()))
				.filter(other -> normalizedNumeroParent.equals(normalizeNumeroParent(other.getNumeroParent())))
				.findFirst()
				.orElse(null);
	}

	private String normalizeNumeroParent(String phone) {
		if (phone == null) {
			return "";
		}
		return phone.replaceAll("\\s+", "").trim();
	}

	private void validateNumeroParentFormat(String normalizedNumeroParent) {
		if (!StringUtils.hasText(normalizedNumeroParent)) {
			return;
		}
		boolean valid = normalizedNumeroParent.matches("^(5[0-9]|2[0-9]|9[0-9])[0-9]{6}$");
		if (!valid) {
			throw new BusinessException(
					"Le numero de parent doit contenir 8 chiffres et commencer par 50-59, 20-29 ou 90-99.");
		}
	}

	private void validateDateNaissance(LocalDate dateNaissance) {
		if (dateNaissance == null) {
			return;
		}
		LocalDate maxAllowed = LocalDate.now().minusYears(4);
		if (dateNaissance.isAfter(maxAllowed)) {
			throw new BusinessException("La date de naissance doit correspondre à un âge d'au moins 4 ans.");
		}
	}
}
