package com.alkemy.java.budgetManager.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.exceptions.PersonNotFound;

public interface IPersonService {

	void savePerson(PersonEntity personEntity);

	PersonEntity getPersonById(Long id) throws PersonNotFound;

	void deletePersonById(Long id) throws PersonNotFound;

	Page<PersonEntity> getListPerson(Pageable pageable);
}
