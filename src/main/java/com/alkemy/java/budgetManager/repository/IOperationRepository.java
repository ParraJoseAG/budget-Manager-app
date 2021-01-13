package com.alkemy.java.budgetManager.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alkemy.java.budgetManager.entities.OperationEntity;

@Repository
public interface IOperationRepository extends JpaRepository<OperationEntity, Long> {

	@Query(value = "SELECT * FROM operations WHERE person_id = :idPerson ORDER BY date DESC LIMIT 10", nativeQuery = true)
	List<OperationEntity> findLastTenOperations(@Param("idPerson") Long id);

	@Query(value = "SELECT * FROM operations WHERE person_id = :idPerson AND type='0' ORDER BY date DESC", nativeQuery = true)
	Page<OperationEntity> findAllOperationIngress(@Param("idPerson") Long id, Pageable pageable);

	@Query(value = "SELECT * FROM operations WHERE person_id = :idPerson AND type='1' ORDER BY date DESC", nativeQuery = true)
	Page<OperationEntity> findAllOperationExpenses(@Param("idPerson") Long id, Pageable pageable);

	@Query(value = "SELECT SUM(amount) AS totalIngress FROM operations WHERE person_id = :idPerson AND type='0'", nativeQuery = true)
	BigDecimal totalIngress(@Param("idPerson") Long id);

	@Query(value = "SELECT SUM(amount) AS totalExpenses FROM operations WHERE person_id = :idPerson AND type='1'", nativeQuery = true)
	BigDecimal totalExpenses(@Param("idPerson") Long id);

}
