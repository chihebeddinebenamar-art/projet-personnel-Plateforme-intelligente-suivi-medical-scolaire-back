package tn.educanet.pfe.specification;

import org.springframework.data.jpa.domain.Specification;

import tn.educanet.pfe.persistence.User;

public abstract class UserSpecification {

	public static Specification<User> perFirstName(final String firstName) {
		return (root, arg1, cb) -> cb.like(root.get("firstName"), firstName);
	}
	
	
}
