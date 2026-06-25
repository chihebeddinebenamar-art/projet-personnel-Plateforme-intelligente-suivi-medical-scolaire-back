package tn.educanet.pfe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.educanet.pfe.persistence.EleveCarnetNumeriquePhoto;

public interface EleveCarnetNumeriquePhotoRepository extends JpaRepository<EleveCarnetNumeriquePhoto, Long> {

	List<EleveCarnetNumeriquePhoto> findByEleve_IdOrderBySortIndexAsc(Long eleveId);

	long countByEleve_Id(Long eleveId);

	void deleteByEleve_Id(Long eleveId);
}
