package tn.educanet.pfe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import tn.educanet.pfe.persistence.EleveMaladie;

public interface EleveMaladieRepository extends JpaRepository<EleveMaladie, Long>, JpaSpecificationExecutor<EleveMaladie> {

	List<EleveMaladie> findByEleveIdOrderByTypeAscLibelleAsc(Long eleveId);

	Optional<EleveMaladie> findByIdAndEleveId(Long id, Long eleveId);
}
