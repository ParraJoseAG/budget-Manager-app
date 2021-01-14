package com.alkemy.java.budgetManager.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.exceptions.AuthorityNotFoundException;
import com.alkemy.java.budgetManager.exceptions.PersonNotFoundException;

public interface IPersonService {

	void savePerson(PersonEntity personEntity) throws AuthorityNotFoundException;

	PersonEntity getPersonById(Long id) throws PersonNotFoundException;

	void deletePersonById(Long id) throws PersonNotFoundException;

	Page<PersonEntity> getListPerson(Pageable pageable);

	PersonEntity findByUsername(String username);

	PersonEntity findByEmail(String email);
}
