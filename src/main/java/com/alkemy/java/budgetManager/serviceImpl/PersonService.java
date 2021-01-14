package com.alkemy.java.budgetManager.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.alkemy.java.budgetManager.entities.AuthorityEntity;
import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.exceptions.AuthorityNotFoundException;
import com.alkemy.java.budgetManager.exceptions.PersonNotFoundException;
import com.alkemy.java.budgetManager.repository.IPersonRepository;
import com.alkemy.java.budgetManager.service.IAuthorityService;
import com.alkemy.java.budgetManager.service.IPersonService;

@Service
public class PersonService implements IPersonService {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private IAuthorityService authorityService;

	@Autowired
	private IPersonRepository personRepository;

	@Override
	public void savePerson(PersonEntity personEntity) throws AuthorityNotFoundException {

		if (personEntity.getId() == null) {
			personEntity.setEnabled(true);
			personEntity.setPassword(passwordEncoder.encode(personEntity.getPassword()));
		}

		AuthorityEntity authority = authorityService.getByIdAuthority(2L);
		personRepository.save(personEntity);
		PersonEntity person = findByUsername(personEntity.getUsername());
		authorityService.insertRolePerson(person.getId(), authority.getId());

	}

	@Override
	public PersonEntity getPersonById(Long id) throws PersonNotFoundException {

		PersonEntity person = personRepository.findById(id)
				.orElseThrow(() -> new PersonNotFoundException("No existe usuario"));
		return person;

	}

	@Override
	public void deletePersonById(Long id) throws PersonNotFoundException {
		PersonEntity person = personRepository.findById(id)
				.orElseThrow(() -> new PersonNotFoundException("No existe usuario"));

		personRepository.deleteById(id);

	}

	@Override
	public Page<PersonEntity> getListPerson(Pageable pageable) {

		return personRepository.findAll(pageable);
	}

	@Override
	public PersonEntity findByUsername(String username) {

		PersonEntity person = personRepository.findByUsername(username).orElse(null);

		return person;
	}

	@Override
	public PersonEntity findByEmail(String email) {
		PersonEntity person = personRepository.findByEmail(email).orElse(null);
		return person;
	}

}
