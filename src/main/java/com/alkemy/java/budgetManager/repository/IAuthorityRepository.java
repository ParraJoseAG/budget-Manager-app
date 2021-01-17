package com.alkemy.java.budgetManager.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alkemy.java.budgetManager.entities.AuthorityEntity;

@Repository
public interface IAuthorityRepository extends JpaRepository<AuthorityEntity, Long> {

	@Query(value = "SELECT * FROM authorities WHERE id=:id", nativeQuery = true)
	Optional<AuthorityEntity> getByIdAuthority(@Param("id") Long id);

	@Modifying
	@Query(value = "INSERT INTO authorities_users(person_id, authority_id) VALUES (:idPerson,:idAuthority)", nativeQuery = true)
	@Transactional
	void insertRolePerson(@Param("idPerson") Long idPerson, @Param("idAuthority") Long idAuthority);

}
