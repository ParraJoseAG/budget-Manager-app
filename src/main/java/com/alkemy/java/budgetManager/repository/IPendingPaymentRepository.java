package com.alkemy.java.budgetManager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alkemy.java.budgetManager.entities.PendingPaymentEntity;

@Repository
public interface IPendingPaymentRepository extends JpaRepository<PendingPaymentEntity, Long> {

	@Query(value = "SELECT * FROM pending_payments WHERE person_id = :idPerson ORDER BY date_expiration ASC", nativeQuery = true)
	List<PendingPaymentEntity> findPendingPaymentPerson(@Param("idPerson") Long id);
}
