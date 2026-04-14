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

import tn.educanet.pfe.persistence.Vaccination;

@Repository
public interface VaccinationRepository
		extends JpaRepository<Vaccination, Long>, JpaSpecificationExecutor<Vaccination> {

	@Query("SELECT v FROM Vaccination v JOIN FETCH v.eleve e JOIN FETCH e.classe c JOIN FETCH c.niveau JOIN FETCH v.typeVaccin WHERE e.id = :eleveId ORDER BY v.dateVaccination DESC")
	List<Vaccination> findByEleveIdOrderByDateVaccinationDesc(@Param("eleveId") Long eleveId);

	@Query("SELECT v FROM Vaccination v JOIN FETCH v.eleve e JOIN FETCH e.classe c JOIN FETCH c.niveau JOIN FETCH v.typeVaccin WHERE v.id = :id")
	Optional<Vaccination> findDetailById(@Param("id") Long id);

	long countByTypeVaccinId(Long typeVaccinId);

	boolean existsByEleve_IdAndTypeVaccin_Id(Long eleveId, Long typeVaccinId);

	@EntityGraph(attributePaths = { "eleve", "eleve.classe", "eleve.classe.niveau", "typeVaccin" })
	@Override
	List<Vaccination> findAll(Specification<Vaccination> spec);
}
