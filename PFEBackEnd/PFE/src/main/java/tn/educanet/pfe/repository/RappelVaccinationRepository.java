package tn.educanet.pfe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.educanet.pfe.persistence.RappelVaccination;

public interface RappelVaccinationRepository extends JpaRepository<RappelVaccination, Long> {

	List<RappelVaccination> findAllByOrderByDatePrevueDescIdDesc();
}
