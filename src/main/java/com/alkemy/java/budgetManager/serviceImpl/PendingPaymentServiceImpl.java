package com.alkemy.java.budgetManager.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alkemy.java.budgetManager.entities.PendingPaymentEntity;
import com.alkemy.java.budgetManager.exceptions.PendingPaymentNotFoundException;
import com.alkemy.java.budgetManager.repository.IPendingPaymentRepository;
import com.alkemy.java.budgetManager.service.IPendingPaymentService;

@Service
public class PendingPaymentServiceImpl implements IPendingPaymentService {

	@Autowired
	private IPendingPaymentRepository pendingPaymentRepository;

	@Override
	public List<PendingPaymentEntity> listPendingPayments(Long id) {
		List<PendingPaymentEntity> listPendingPayments = pendingPaymentRepository.findPendingPaymentPerson(id);
		return listPendingPayments;
	}

	@Override
	public void createPendingPayment(PendingPaymentEntity pendingPayment) {
		pendingPaymentRepository.save(pendingPayment);

	}

	@Override
	public PendingPaymentEntity findPendingPaymentEntityById(Long id) throws PendingPaymentNotFoundException {
		Optional<PendingPaymentEntity> pendingPayment = pendingPaymentRepository.findById(id);

		if (pendingPayment.isEmpty()) {
			throw new PendingPaymentNotFoundException("Pago pendiente no esta registrado");
		}

		return pendingPayment.get();
	}

	@Override
	public void deletePendingPaymentEntityById(Long id) throws PendingPaymentNotFoundException {
		Optional<PendingPaymentEntity> pendingPayment = pendingPaymentRepository.findById(id);

		if (pendingPayment.isEmpty()) {
			throw new PendingPaymentNotFoundException("Pago pendiente no esta registrado");
		}

		pendingPaymentRepository.deleteById(id);

	}

}
