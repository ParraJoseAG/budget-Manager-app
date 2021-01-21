package com.alkemy.java.budgetManager.serviceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alkemy.java.budgetManager.entities.FixedTermEntity;
import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.exceptions.FixedTermNotFoundException;
import com.alkemy.java.budgetManager.repository.IFixedTermRepository;
import com.alkemy.java.budgetManager.service.IFixedTermService;
import com.alkemy.java.budgetManager.service.IOperationService;

@Service
public class FixedTermServiceImpl implements IFixedTermService {

	@Autowired
	private IFixedTermRepository fixedTermRepository;

	@Autowired
	private IOperationService operationService;

	@Override
	public List<FixedTermEntity> getListFixedTermPerson(Long id) {

		return fixedTermRepository.findAllFixedTerm(id);
	}

	@Override
	public void saveFixedTerm(FixedTermEntity fixedTerm) throws Exception {

		PersonEntity person = new PersonEntity();
		person = fixedTerm.getPerson();
		Long idPerson = person.getId();

		BigDecimal amountAvailable = operationService.getCurrentBalance(idPerson);
		BigDecimal amountFixedTerm = fixedTerm.getAmountFixedTerm();

		if (amountFixedTerm.compareTo(amountAvailable) > 0) {
			throw new Exception();
		}

		fixedTermRepository.save(fixedTerm);
	}

	@Override
	public FixedTermEntity getByIdFixedTerm(Long id) throws FixedTermNotFoundException {

		Optional<FixedTermEntity> optionalfixedTerm = fixedTermRepository.findById(id);

		if (optionalfixedTerm.isEmpty()) {
			throw new FixedTermNotFoundException("Plazo fijo no existe");
		}

		return optionalfixedTerm.get();
	}

	@Override
	public void deleteByIdFixedTerm(Long id) {
		Optional<FixedTermEntity> optionalfixedTerm = fixedTermRepository.findById(id);

		if (optionalfixedTerm.isEmpty()) {
			throw new FixedTermNotFoundException("Plazo fijo no existe");
		}
		fixedTermRepository.deleteById(id);
	}

}
