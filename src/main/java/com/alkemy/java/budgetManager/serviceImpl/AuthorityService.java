package com.alkemy.java.budgetManager.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alkemy.java.budgetManager.entities.AuthorityEntity;
import com.alkemy.java.budgetManager.exceptions.AuthorityNotFoundException;
import com.alkemy.java.budgetManager.repository.IAuthorityRepository;
import com.alkemy.java.budgetManager.service.IAuthorityService;

@Service
public class AuthorityService implements IAuthorityService {

	@Autowired
	private IAuthorityRepository authorityRepository;

	@Override
	public AuthorityEntity getByIdAuthority(Long id) throws AuthorityNotFoundException {

		AuthorityEntity authority = authorityRepository.getByIdAuthority(id)
				.orElseThrow(() -> new AuthorityNotFoundException("Rol no encontrado"));

		return authority;
	}

	@Override
	public void insertRolePerson(Long idPerson, Long idAuthority) {
		authorityRepository.insertRolePerson(idPerson, idAuthority);

	}

}
