package com.alkemy.java.budgetManager.service;

import java.util.List;
import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alkemy.java.budgetManager.entities.OperationEntity;

public interface IOperationService {

	void saveOperation(OperationEntity operationEntity) throws Exception;

	void saveSendingMoney(OperationEntity operationEntity) throws Exception;

	void saveReceiveMoney(OperationEntity operationEntity);

	List<OperationEntity> getLastTenOperation(Long id);

	Page<OperationEntity> getListOperationIngress(Long id, Pageable pageable);

	Page<OperationEntity> getListOperationExpenses(Long id, Pageable pageable);

	BigDecimal getCurrentBalance(Long id);

	BigDecimal getTotalIngress(Long id);

	BigDecimal getTotalExpenses(Long id);

}
