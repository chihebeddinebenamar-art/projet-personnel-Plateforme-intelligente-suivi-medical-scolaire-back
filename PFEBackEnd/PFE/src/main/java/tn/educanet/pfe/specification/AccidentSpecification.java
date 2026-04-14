package tn.educanet.pfe.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import tn.educanet.pfe.persistence.Accident;
import tn.educanet.pfe.persistence.Classe;
import tn.educanet.pfe.persistence.Eleve;

public abstract class AccidentSpecification {

	public static Specification<Accident> filtres(Long niveauId, Long classeId, String q) {
		return (root, query, cb) -> {
			Join<Accident, Eleve> eleve = root.join("eleve", JoinType.INNER);
			Join<Eleve, Classe> classe = eleve.join("classe", JoinType.INNER);
			var niveau = classe.join("niveau", JoinType.INNER);
			if (query != null && Accident.class.equals(query.getResultType())) {
				query.distinct(true);
				query.orderBy(cb.desc(root.get("dateAccident")));
			}
			var p = cb.conjunction();
			if (niveauId != null) {
				p = cb.and(p, cb.equal(classe.get("niveau").get("id"), niveauId));
			}
			if (classeId != null) {
				p = cb.and(p, cb.equal(classe.get("id"), classeId));
			}
			if (StringUtils.hasText(q)) {
				String like = "%" + q.trim().toLowerCase() + "%";
				Expression<String> dAcc = cb.function("date_format", String.class, root.get("dateAccident"),
						cb.literal("%Y-%m-%d"));
				p = cb.and(p, cb.or(
						cb.like(cb.lower(eleve.get("nom")), like),
						cb.like(cb.lower(eleve.get("prenom")), like),
						cb.like(cb.lower(eleve.get("matricule")), like),
						cb.like(cb.lower(classe.get("nom")), like),
						cb.like(cb.lower(niveau.get("nom")), like),
						cb.like(cb.lower(root.get("description")), like),
						cb.like(cb.lower(root.get("diagnostic")), like),
						cb.like(cb.lower(root.get("traitement")), like),
						cb.like(cb.lower(root.get("etat")), like),
						cb.like(cb.lower(dAcc), like)));
			}
			return p;
		};
	}
}
