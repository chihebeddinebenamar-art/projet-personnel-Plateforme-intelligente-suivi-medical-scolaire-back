package tn.educanet.pfe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.educanet.pfe.persistence.ElevePhotoProfil;

public interface ElevePhotoProfilRepository extends JpaRepository<ElevePhotoProfil, Long> {

	Optional<ElevePhotoProfil> findByEleveId(Long eleveId);

	boolean existsByEleveId(Long eleveId);

	void deleteByEleveId(Long eleveId);
}
