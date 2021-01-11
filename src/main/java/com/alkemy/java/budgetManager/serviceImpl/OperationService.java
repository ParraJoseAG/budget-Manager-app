package com.alkemy.java.budgetManager.serviceImpl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.alkemy.java.budgetManager.entities.OperationEntity;
import com.alkemy.java.budgetManager.repository.IOperationRepository;
import com.alkemy.java.budgetManager.service.IOperationService;

@Service
public class OperationService implements IOperationService {

	@Autowired
	private IOperationRepository operationRepository;

	@Override
	public void saveOperation(OperationEntity operationEntity) {

		operationRepository.save(operationEntity);
	}

	@Override
	public List<OperationEntity> getLastTenOperation(Long id) {

		return operationRepository.findLastTenOperations(id);
	}

	@Override
	public Page<OperationEntity> getListOperationIngress(Long id, Pageable pageable) {

		return operationRepository.findAllOperationIngress(id, pageable);
	}

	@Override
	public Page<OperationEntity> getListOperationExpenses(Long id, Pageable pageable) {

		return operationRepository.findAllOperationExpenses(id, pageable);
	}

	@Override
	public BigDecimal getCurrentBalance(Long id) {

		BigDecimal balance = getTotalIngress(id).subtract(getTotalExpenses(id));

		return balance;
	}

	@Override
	public BigDecimal getTotalIngress(Long id) {

		BigDecimal ingress = new BigDecimal(0);
		BigDecimal totalIngress = operationRepository.totalIngress(id);

		if (totalIngress != null) {
			ingress = totalIngress;
		}
		return ingress;
	}

	@Override
	public BigDecimal getTotalExpenses(Long id) {

		BigDecimal expenses = new BigDecimal(0);
		BigDecimal totalExpenses = operationRepository.totalExpenses(id);

		if (totalExpenses != null) {
			expenses = totalExpenses;
		}
		return expenses;
	}

}
