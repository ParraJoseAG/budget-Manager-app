package com.alkemy.java.budgetManager.service;

import java.util.List;

import com.alkemy.java.budgetManager.entities.FixedTermEntity;
import com.alkemy.java.budgetManager.exceptions.FixedTermNotFoundException;

public interface IFixedTermService {

	public List<FixedTermEntity> getListFixedTermPerson(Long id);

	public void saveFixedTerm(FixedTermEntity fixedTerm) throws Exception;

	public FixedTermEntity getByIdFixedTerm(Long id) throws FixedTermNotFoundException;

	public void deleteByIdFixedTerm(Long id) throws FixedTermNotFoundException;

}
