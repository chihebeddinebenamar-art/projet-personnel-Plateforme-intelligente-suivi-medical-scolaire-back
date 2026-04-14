package tn.educanet.pfe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import tn.educanet.pfe.persistence.Eleve;

@Repository
public interface EleveRepository extends JpaRepository<Eleve, Long>, JpaSpecificationExecutor<Eleve> {

	List<Eleve> findByClasseIdOrderByNomAscPrenomAsc(Long classeId);

	boolean existsByMatricule(String matricule);

	boolean existsByMatriculeAndIdNot(String matricule, Long id);
}
