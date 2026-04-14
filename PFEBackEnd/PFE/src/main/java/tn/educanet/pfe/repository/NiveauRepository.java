package tn.educanet.pfe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import tn.educanet.pfe.persistence.Niveau;

@Repository
public interface NiveauRepository extends JpaRepository<Niveau, Long>, JpaSpecificationExecutor<Niveau> {

	List<Niveau> findByAnneeScolaireContainingIgnoreCase(String annee);

	List<Niveau> findByNomContainingIgnoreCase(String nom);
}
