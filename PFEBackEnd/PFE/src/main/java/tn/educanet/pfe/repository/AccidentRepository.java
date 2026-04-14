package tn.educanet.pfe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.educanet.pfe.persistence.Accident;

@Repository
public interface AccidentRepository extends JpaRepository<Accident, Long>, JpaSpecificationExecutor<Accident> {

	@Query("SELECT a FROM Accident a JOIN FETCH a.eleve e JOIN FETCH e.classe cl JOIN FETCH cl.niveau WHERE e.id = :eleveId ORDER BY a.dateAccident DESC")
	List<Accident> findByEleveIdOrderByDateAccidentDesc(@Param("eleveId") Long eleveId);

	@Query("SELECT a FROM Accident a JOIN FETCH a.eleve e JOIN FETCH e.classe cl JOIN FETCH cl.niveau WHERE a.id = :id")
	Optional<Accident> findDetailById(@Param("id") Long id);

	@EntityGraph(attributePaths = { "eleve", "eleve.classe", "eleve.classe.niveau" })
	@Override
	List<Accident> findAll(Specification<Accident> spec);
}
