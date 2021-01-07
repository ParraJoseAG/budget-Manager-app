package com.alkemy.java.budgetManager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alkemy.java.budgetManager.entities.PersonEntity;

@Repository
public interface IPersonRepository extends JpaRepository<PersonEntity, Long> {

}
