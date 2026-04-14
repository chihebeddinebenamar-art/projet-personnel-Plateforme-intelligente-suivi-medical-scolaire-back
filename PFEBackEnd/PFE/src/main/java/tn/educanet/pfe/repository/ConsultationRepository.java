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

import tn.educanet.pfe.persistence.Consultation;

@Repository
public interface ConsultationRepository
		extends JpaRepository<Consultation, Long>, JpaSpecificationExecutor<Consultation> {

	@Query("SELECT c FROM Consultation c JOIN FETCH c.eleve e JOIN FETCH e.classe cl JOIN FETCH cl.niveau WHERE e.id = :eleveId ORDER BY c.dateConsultation DESC")
	List<Consultation> findByEleveIdOrderByDateConsultationDesc(@Param("eleveId") Long eleveId);

	@Query("SELECT c FROM Consultation c JOIN FETCH c.eleve e JOIN FETCH e.classe cl JOIN FETCH cl.niveau WHERE c.id = :id")
	Optional<Consultation> findDetailById(@Param("id") Long id);

	@EntityGraph(attributePaths = { "eleve", "eleve.classe", "eleve.classe.niveau" })
	@Override
	List<Consultation> findAll(Specification<Consultation> spec);
}
