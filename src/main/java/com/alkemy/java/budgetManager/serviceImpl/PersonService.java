package com.alkemy.java.budgetManager.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.exceptions.PersonNotFound;
import com.alkemy.java.budgetManager.repository.IPersonRepository;
import com.alkemy.java.budgetManager.service.IPersonService;

@Service
public class PersonService implements IPersonService {

	@Autowired
	private IPersonRepository personRepository;

	@Override
	public void savePerson(PersonEntity personEntity) {

		personRepository.save(personEntity);
	}

	@Override
	public PersonEntity getPersonById(Long id) throws PersonNotFound {

		Optional<PersonEntity> personEntity = personRepository.findById(id);

		if (personEntity.isEmpty()) {

			throw new PersonNotFound("Persona no registrada en la App");
		} else {
			return personEntity.get();
		}

	}

	@Override
	public void deletePersonById(Long id) throws PersonNotFound {
		Optional<PersonEntity> personEntity = personRepository.findById(id);

		if (personEntity.isEmpty()) {

			throw new PersonNotFound("Persona no registrada en la App");
		}

		personRepository.deleteById(id);

	}

	@Override
	public Page<PersonEntity> getListPerson(Pageable pageable) {

		return personRepository.findAll(pageable);
	}

}
