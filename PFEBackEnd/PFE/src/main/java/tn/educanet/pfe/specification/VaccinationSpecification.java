package tn.educanet.pfe.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import tn.educanet.pfe.persistence.Classe;
import tn.educanet.pfe.persistence.Eleve;
import tn.educanet.pfe.persistence.TypeVaccin;
import tn.educanet.pfe.persistence.Vaccination;

public abstract class VaccinationSpecification {

	public static Specification<Vaccination> filtres(Long niveauId, Long classeId, Long typeVaccinId, String q,
			String numeroLot) {
		return (root, query, cb) -> {
			Join<Vaccination, Eleve> eleve = root.join("eleve", JoinType.INNER);
			Join<Eleve, Classe> classe = eleve.join("classe", JoinType.INNER);
			classe.join("niveau", JoinType.INNER);
			Join<Vaccination, TypeVaccin> typeVaccin = root.join("typeVaccin", JoinType.INNER);
			if (query != null && Vaccination.class.equals(query.getResultType())) {
				query.distinct(true);
				query.orderBy(cb.desc(root.get("dateVaccination")));
			}
			var p = cb.conjunction();
			if (niveauId != null) {
				p = cb.and(p, cb.equal(classe.get("niveau").get("id"), niveauId));
			}
			if (classeId != null) {
				p = cb.and(p, cb.equal(classe.get("id"), classeId));
			}
			if (typeVaccinId != null) {
				p = cb.and(p, cb.equal(typeVaccin.get("id"), typeVaccinId));
			}
			if (StringUtils.hasText(q)) {
				String like = "%" + q.trim().toLowerCase() + "%";
				var niveauJoin = classe.get("niveau");
				p = cb.and(p, cb.or(
						cb.like(cb.lower(eleve.get("nom")), like),
						cb.like(cb.lower(eleve.get("prenom")), like),
						cb.like(cb.lower(eleve.get("matricule")), like),
						cb.like(cb.lower(root.get("numeroLot")), like),
						cb.like(cb.lower(classe.get("nom")), like),
						cb.like(cb.lower(niveauJoin.get("nom")), like)));
			}
			if (StringUtils.hasText(numeroLot)) {
				String likeLot = "%" + numeroLot.trim().toLowerCase() + "%";
				p = cb.and(p, cb.like(cb.lower(root.get("numeroLot")), likeLot));
			}
			return p;
		};
	}
}
