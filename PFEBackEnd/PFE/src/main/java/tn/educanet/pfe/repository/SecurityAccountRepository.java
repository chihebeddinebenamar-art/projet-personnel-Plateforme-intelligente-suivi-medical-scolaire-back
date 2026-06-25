package tn.educanet.pfe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.educanet.pfe.persistence.SecurityAccount;

@Repository
public interface SecurityAccountRepository extends JpaRepository<SecurityAccount, Long> {

	Optional<SecurityAccount> findByUsernameIgnoreCase(String username);

	boolean existsByUsernameIgnoreCase(String username);
}
