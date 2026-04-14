package tn.educanet.pfe.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import tn.educanet.pfe.persistence.Classe;
import tn.educanet.pfe.persistence.Eleve;

public abstract class EleveSpecification {

	public static Specification<Eleve> filtres(Long niveauId, Long classeId, String recherche) {
		return (root, query, cb) -> {
			Join<Eleve, Classe> classe = root.join("classe", JoinType.INNER);
			classe.join("niveau", JoinType.INNER);
			if (query != null && Eleve.class.equals(query.getResultType())) {
				query.distinct(true);
			}
			var p = cb.conjunction();
			if (niveauId != null) {
				p = cb.and(p, cb.equal(classe.get("niveau").get("id"), niveauId));
			}
			if (classeId != null) {
				p = cb.and(p, cb.equal(classe.get("id"), classeId));
			}
			if (StringUtils.hasText(recherche)) {
				String like = "%" + recherche.trim().toLowerCase() + "%";
				p = cb.and(p, cb.or(
						cb.like(cb.lower(root.get("nom")), like),
						cb.like(cb.lower(root.get("prenom")), like),
						cb.like(cb.lower(root.get("matricule")), like)));
			}
			return p;
		};
	}
}
