package com.alkemy.java.budgetManager.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alkemy.java.budgetManager.entities.OperationEntity;

public interface IOperationService {

	void saveOperation(OperationEntity operationEntity);

	List<OperationEntity> getLastTenOperation(Long id);

	Page<OperationEntity> getListOperationIngress(Long id, Pageable pageable);

	Page<OperationEntity> getListOperationExpenses(Long id, Pageable pageable);

}
