package tn.educanet.pfe.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import tn.educanet.pfe.persistence.Classe;
import tn.educanet.pfe.persistence.Consultation;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.persistence.Niveau;

public abstract class ConsultationSpecification {

	public static Specification<Consultation> filtres(Long niveauId, Long classeId, String q) {
		return (root, query, cb) -> {
			Join<Consultation, Eleve> eleve = root.join("eleve", JoinType.INNER);
			Join<Eleve, Classe> classe = eleve.join("classe", JoinType.INNER);
			Join<Classe, Niveau> niveau = classe.join("niveau", JoinType.INNER);
			if (query != null && Consultation.class.equals(query.getResultType())) {
				query.distinct(true);
				query.orderBy(cb.desc(root.get("dateConsultation")));
			}
			var p = cb.conjunction();
			if (niveauId != null) {
				p = cb.and(p, cb.equal(niveau.get("id"), niveauId));
			}
			if (classeId != null) {
				p = cb.and(p, cb.equal(classe.get("id"), classeId));
			}
			if (StringUtils.hasText(q)) {
				String like = "%" + q.trim().toLowerCase() + "%";
				p = cb.and(p, buildTextPredicate(cb, root, eleve, classe, niveau, like));
			}
			return p;
		};
	}

	/** Recherche par sous-chaîne (LIKE %mot%) sur tous les champs texte + dates/températion (MySQL). */
	private static Predicate buildTextPredicate(CriteriaBuilder cb, Root<Consultation> root,
			Join<Consultation, Eleve> eleve, Join<Eleve, Classe> classe, Join<Classe, Niveau> niveau, String like) {
		var ors = new java.util.ArrayList<Predicate>();
		ors.add(cb.like(cb.lower(eleve.get("nom")), like));
		ors.add(cb.like(cb.lower(eleve.get("prenom")), like));
		ors.add(cb.like(cb.lower(eleve.get("matricule")), like));
		ors.add(cb.like(cb.lower(classe.get("nom")), like));
		ors.add(cb.like(cb.lower(niveau.get("nom")), like));
		ors.add(cb.like(cb.lower(root.get("rapport")), like));
		ors.add(cb.like(cb.lower(root.get("traitement")), like));
		ors.add(cb.like(cb.lower(cb.function("concat", String.class, cb.literal(""), root.get("temperature"))), like));
		Expression<String> d1 = cb.function("date_format", String.class, root.get("dateConsultation"),
				cb.literal("%Y-%m-%d"));
		Expression<String> d2 = cb.function("date_format", String.class, root.get("prochaineConsultation"),
				cb.literal("%Y-%m-%d"));
		ors.add(cb.like(cb.lower(d1), like));
		ors.add(cb.like(cb.lower(d2), like));
		Expression<String> vom = cb.function("if", String.class, root.get("vomissement"), cb.literal("oui"),
				cb.literal("non"));
		Expression<String> dia = cb.function("if", String.class, root.get("diarrhee"), cb.literal("oui"),
				cb.literal("non"));
		ors.add(cb.like(cb.lower(vom), like));
		ors.add(cb.like(cb.lower(dia), like));
		return cb.or(ors.toArray(Predicate[]::new));
	}
}
