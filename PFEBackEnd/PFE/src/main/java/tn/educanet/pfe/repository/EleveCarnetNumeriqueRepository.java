package tn.educanet.pfe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.educanet.pfe.persistence.EleveCarnetNumerique;

public interface EleveCarnetNumeriqueRepository extends JpaRepository<EleveCarnetNumerique, Long> {

	Optional<EleveCarnetNumerique> findByEleveId(Long eleveId);

	boolean existsByEleveId(Long eleveId);

	void deleteByEleveId(Long eleveId);
}
