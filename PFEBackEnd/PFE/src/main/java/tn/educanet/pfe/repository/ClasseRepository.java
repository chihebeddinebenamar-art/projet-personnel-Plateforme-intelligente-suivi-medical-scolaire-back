package tn.educanet.pfe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.educanet.pfe.persistence.Classe;

@Repository
public interface ClasseRepository extends JpaRepository<Classe, Long>, JpaSpecificationExecutor<Classe> {

	@Query("SELECT c FROM Classe c LEFT JOIN FETCH c.niveau ORDER BY c.nom ASC")
	List<Classe> findAllFetchNiveau();

	@Query("SELECT c FROM Classe c JOIN FETCH c.niveau WHERE c.niveau.id = :niveauId ORDER BY c.nom ASC")
	List<Classe> findByNiveauIdOrderByNomAsc(@Param("niveauId") Long niveauId);

	@Query("SELECT c FROM Classe c JOIN FETCH c.niveau WHERE c.id = :id")
	Optional<Classe> findByIdFetchNiveau(@Param("id") Long id);
}
