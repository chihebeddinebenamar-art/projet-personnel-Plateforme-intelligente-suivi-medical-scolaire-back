package tn.educanet.pfe.specification;

import org.springframework.data.jpa.domain.Specification;

import tn.educanet.pfe.persistence.User;

public abstract class UserSpecification {

	/**
	 * Critère sur le prénom (sans méta-modèle généré {@code User_}) pour éviter
	 * les conflits APT Maven / IntelliJ avec hibernate-jpamodelgen.
	 */
	public static Specification<User> perFirstName(final String firstName) {
		return (root, arg1, cb) -> cb.like(root.get("firstName"), firstName);
	}
}
