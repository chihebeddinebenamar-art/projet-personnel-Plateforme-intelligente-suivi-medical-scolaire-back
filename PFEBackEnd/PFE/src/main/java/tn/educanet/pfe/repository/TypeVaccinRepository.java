package tn.educanet.pfe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.educanet.pfe.persistence.TypeVaccin;

@Repository
public interface TypeVaccinRepository extends JpaRepository<TypeVaccin, Long> {
}
