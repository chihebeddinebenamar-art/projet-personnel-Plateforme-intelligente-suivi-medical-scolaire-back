package tn.educanet.pfe.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import tn.educanet.pfe.persistence.Classe;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.persistence.EleveMaladie;
import tn.educanet.pfe.persistence.Niveau;

public abstract class EleveMaladieSpecification {

	public static Specification<EleveMaladie> filtres(Long niveauId, Long classeId, String q) {
		return (root, query, cb) -> {
			Join<EleveMaladie, Eleve> eleve = root.join("eleve", JoinType.INNER);
			Join<Eleve, Classe> classe = eleve.join("classe", JoinType.INNER);
			Join<Classe, Niveau> niveau = classe.join("niveau", JoinType.INNER);
			if (query != null && EleveMaladie.class.equals(query.getResultType())) {
				query.distinct(true);
				query.orderBy(cb.asc(niveau.get("nom")), cb.asc(classe.get("nom")), cb.asc(eleve.get("nom")),
						cb.asc(eleve.get("prenom")), cb.asc(root.get("libelle")));
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
				var matStr = cb.function("concat", String.class, cb.literal(""), eleve.get("matricule"));
				var detStr = cb.function("concat", String.class, cb.literal(""), root.get("details"));
				p = cb.and(p,
						cb.or(cb.like(cb.lower(eleve.get("nom")), like), cb.like(cb.lower(eleve.get("prenom")), like),
								cb.like(cb.lower(matStr), like), cb.like(cb.lower(classe.get("nom")), like),
								cb.like(cb.lower(niveau.get("nom")), like), cb.like(cb.lower(root.get("libelle")), like),
								cb.like(cb.lower(detStr), like)));
			}
			return p;
		};
	}
}
