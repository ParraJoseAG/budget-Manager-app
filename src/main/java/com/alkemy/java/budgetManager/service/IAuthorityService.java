package com.alkemy.java.budgetManager.service;

import com.alkemy.java.budgetManager.entities.AuthorityEntity;
import com.alkemy.java.budgetManager.exceptions.AuthorityNotFoundException;

public interface IAuthorityService {

	public AuthorityEntity getByIdAuthority(Long id) throws AuthorityNotFoundException;

	public void insertRolePerson(Long idPerson, Long idAuthority);
}
