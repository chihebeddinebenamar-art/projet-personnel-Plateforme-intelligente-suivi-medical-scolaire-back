package tn.educanet.pfe.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.educanet.pfe.persistence.ParentNotification;

@Repository
public interface ParentNotificationRepository extends JpaRepository<ParentNotification, Long> {

	List<ParentNotification> findByNumeroParentOrderByCreatedAtDesc(String numeroParent);

	boolean existsByEleveIdAndTypeEvenementAndCreatedAtAfter(Long eleveId, String typeEvenement,
			LocalDateTime createdAtAfter);
}
