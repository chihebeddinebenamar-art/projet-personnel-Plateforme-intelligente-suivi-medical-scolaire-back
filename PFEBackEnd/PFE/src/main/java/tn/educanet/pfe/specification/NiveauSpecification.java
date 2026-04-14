package tn.educanet.pfe.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import tn.educanet.pfe.persistence.Niveau;

public abstract class NiveauSpecification {

	public static Specification<Niveau> filtres(String annee, String nom) {
		return (root, query, cb) -> {
			var p = cb.conjunction();
			if (StringUtils.hasText(annee)) {
				p = cb.and(p, cb.like(cb.lower(root.get("anneeScolaire")), "%" + annee.trim().toLowerCase() + "%"));
			}
			if (StringUtils.hasText(nom)) {
				p = cb.and(p, cb.like(cb.lower(root.get("nom")), "%" + nom.trim().toLowerCase() + "%"));
			}
			return p;
		};
	}
}
