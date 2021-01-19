package com.alkemy.java.budgetManager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alkemy.java.budgetManager.entities.FixedTermEntity;

public interface IFixedTermRepository extends JpaRepository<FixedTermEntity, Long> {

	@Query(value = "SELECT * FROM deposits_fixed WHERE person_id = :idPerson ORDER BY start_date DESC", nativeQuery = true)
	List<FixedTermEntity> findAllFixedTerm(@Param("idPerson") Long id);
}
