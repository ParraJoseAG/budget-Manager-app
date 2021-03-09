package com.alkemy.java.budgetManager.service;

import java.util.List;

import com.alkemy.java.budgetManager.entities.PendingPaymentEntity;
import com.alkemy.java.budgetManager.exceptions.PendingPaymentNotFoundException;

public interface IPendingPaymentService {

	public List<PendingPaymentEntity> listPendingPayments(Long id);

	public void createPendingPayment(PendingPaymentEntity pendingPayment);

	public PendingPaymentEntity findPendingPaymentEntityById(Long id) throws PendingPaymentNotFoundException;

	public void deletePendingPaymentEntityById(Long id) throws PendingPaymentNotFoundException;
}
