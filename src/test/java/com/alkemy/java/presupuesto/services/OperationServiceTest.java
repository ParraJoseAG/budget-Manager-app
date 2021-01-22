package com.alkemy.java.presupuesto.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.alkemy.java.budgetManager.entities.OperationEntity;
import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.models.Type;
import com.alkemy.java.budgetManager.repository.IOperationRepository;
import com.alkemy.java.budgetManager.serviceImpl.OperationService;

public class OperationServiceTest {

	@Mock
	private IOperationRepository operationRepositoryMock;

	@InjectMocks
	private OperationService operationService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = Exception.class)
	public void testSaveOperationShouldThrowsExceptionBalanceMinorEgreso() throws Exception {

		BigDecimal totalIngress = new BigDecimal(2);
		BigDecimal totalExpenses = new BigDecimal(1);
		BigDecimal amountEgreso = new BigDecimal(4);

		PersonEntity personMock = Mockito.mock(PersonEntity.class);
		personMock.setId(1L);
		OperationEntity operationMock = Mockito.mock(OperationEntity.class);

		Mockito.when(operationMock.getPerson()).thenReturn(personMock);
		Mockito.when(operationMock.getType()).thenReturn(Type.EXPENSES);
		Mockito.when(personMock.getId()).thenReturn(1L);

		Mockito.when(operationMock.getAmount()).thenReturn(amountEgreso);
		Mockito.when(operationService.getTotalIngress(Mockito.anyLong())).thenReturn(totalIngress);
		Mockito.when(operationService.getTotalExpenses(Mockito.anyLong())).thenReturn(totalExpenses);
		operationService.saveOperation(operationMock);

		verify(operationRepositoryMock, never()).save(Mockito.any(OperationEntity.class));
	}

	@Test
	public void testSaveOperationTypeExpenses() throws Exception {

		BigDecimal totalIngress = new BigDecimal(2);
		BigDecimal totalExpenses = new BigDecimal(1);
		BigDecimal amountExpenses = new BigDecimal(0);

		PersonEntity personMock = Mockito.mock(PersonEntity.class);
		personMock.setId(1L);
		OperationEntity operationMock = Mockito.mock(OperationEntity.class);

		Mockito.when(operationMock.getPerson()).thenReturn(personMock);
		Mockito.when(operationMock.getType()).thenReturn(Type.EXPENSES);
		Mockito.when(personMock.getId()).thenReturn(1L);

		Mockito.when(operationMock.getAmount()).thenReturn(amountExpenses);
		Mockito.when(operationService.getTotalIngress(Mockito.anyLong())).thenReturn(totalIngress);
		Mockito.when(operationService.getTotalExpenses(Mockito.anyLong())).thenReturn(totalExpenses);
		operationService.saveOperation(operationMock);

		verify(operationRepositoryMock, Mockito.times(1)).save(Mockito.any(OperationEntity.class));
	}

	@Test
	public void testSaveOperationTypeIngress() throws Exception {

		PersonEntity personMock = Mockito.mock(PersonEntity.class);
		personMock.setId(1L);
		OperationEntity operationMock = Mockito.mock(OperationEntity.class);

		Mockito.when(operationMock.getPerson()).thenReturn(personMock);
		Mockito.when(operationMock.getType()).thenReturn(Type.INGRESS);

		operationService.saveOperation(operationMock);

		verify(operationRepositoryMock, Mockito.times(1)).save(Mockito.any(OperationEntity.class));
	}

	@Test
	public void testGetLastTenOperation() {
		List<OperationEntity> listOperations = new ArrayList<OperationEntity>();

		OperationEntity op1 = new OperationEntity();
		op1.setId(1L);
		op1.setAmount(new BigDecimal(1));

		OperationEntity op2 = new OperationEntity();
		op2.setId(2L);
		op2.setAmount(new BigDecimal(2));

		listOperations.add(op1);
		listOperations.add(op2);

		Mockito.when(operationRepositoryMock.findLastTenOperations(Mockito.anyLong())).thenReturn(listOperations);

		List<OperationEntity> expected = operationService.getLastTenOperation(Mockito.anyLong());

		assertEquals(expected, listOperations);
		assertEquals(expected.size(), listOperations.size());
		assertEquals(expected.size(), 2);

		verify(operationRepositoryMock, Mockito.times(1)).findLastTenOperations(Mockito.anyLong());

	}

	@Test
	public void testGetListOperationIngress() {
		List<OperationEntity> listOperations = new ArrayList<OperationEntity>();

		OperationEntity op1 = new OperationEntity();
		op1.setId(1L);
		op1.setAmount(new BigDecimal(1));

		OperationEntity op2 = new OperationEntity();
		op2.setId(2L);
		op2.setAmount(new BigDecimal(2));

		listOperations.add(op1);
		listOperations.add(op2);

		Page<OperationEntity> page = new PageImpl<OperationEntity>(listOperations);

		Mockito.when(operationRepositoryMock.findAllOperationIngress(0L, null)).thenReturn(page);

		Page<OperationEntity> expected = operationService.getListOperationIngress(Mockito.anyLong(),
				Mockito.any(PageRequest.class));

		assertEquals(expected, page);
		assertEquals(expected.getTotalPages(), page.getTotalPages());
		assertEquals(expected.getContent().size(), 2);

		verify(operationRepositoryMock, Mockito.times(1)).findAllOperationIngress(0L, null);
	}

	@Test
	public void testGetListOperationExpenses() {
		List<OperationEntity> listOperations = new ArrayList<OperationEntity>();

		OperationEntity op1 = new OperationEntity();
		op1.setId(1L);
		op1.setAmount(new BigDecimal(1));

		OperationEntity op2 = new OperationEntity();
		op2.setId(2L);
		op2.setAmount(new BigDecimal(2));

		listOperations.add(op1);
		listOperations.add(op2);

		Page<OperationEntity> page = new PageImpl<OperationEntity>(listOperations);

		Mockito.when(operationRepositoryMock.findAllOperationExpenses(0L, null)).thenReturn(page);

		Page<OperationEntity> expected = operationService.getListOperationExpenses(Mockito.anyLong(),
				Mockito.any(PageRequest.class));

		assertEquals(expected, page);
		assertEquals(expected.getTotalPages(), page.getTotalPages());
		assertEquals(expected.getContent().size(), 2);

		verify(operationRepositoryMock, Mockito.times(1)).findAllOperationExpenses(0L, null);
	}

	@Test
	public void testGetTotalIngressIsZero() {

		Mockito.when(operationRepositoryMock.totalIngress(Mockito.anyLong())).thenReturn(null);
		BigDecimal expected = operationService.getTotalIngress(Mockito.anyLong());
		assertEquals(expected, new BigDecimal(0));
		verify(operationRepositoryMock, Mockito.times(1)).totalIngress(Mockito.anyLong());
	}

	@Test
	public void testGetTotalIngress() {

		Mockito.when(operationRepositoryMock.totalIngress(Mockito.anyLong())).thenReturn(new BigDecimal(1));
		BigDecimal expected = operationService.getTotalIngress(Mockito.anyLong());
		assertEquals(expected, new BigDecimal(1));
		verify(operationRepositoryMock, Mockito.times(1)).totalIngress(Mockito.anyLong());
	}

	@Test
	public void testGetTotalExpensesIsZero() {

		Mockito.when(operationRepositoryMock.totalExpenses(Mockito.anyLong())).thenReturn(null);
		BigDecimal expected = operationService.getTotalExpenses(Mockito.anyLong());
		assertEquals(expected, new BigDecimal(0));
		verify(operationRepositoryMock, Mockito.times(1)).totalExpenses(Mockito.anyLong());
	}

	@Test
	public void testGetTotalExpenses() {

		Mockito.when(operationRepositoryMock.totalExpenses(Mockito.anyLong())).thenReturn(new BigDecimal(1));
		BigDecimal expected = operationService.getTotalExpenses(Mockito.anyLong());
		assertEquals(expected, new BigDecimal(1));
		verify(operationRepositoryMock, Mockito.times(1)).totalExpenses(Mockito.anyLong());
	}

	@Test(expected = Exception.class)
	public void testSaveSendingMoneyShouldThrowsExceptionBalanceMinorAmountSend() throws Exception {

		BigDecimal totalIngress = new BigDecimal(2);
		BigDecimal totalExpenses = new BigDecimal(1);
		BigDecimal amountSend = new BigDecimal(5);

		OperationEntity operationMock = Mockito.mock(OperationEntity.class);
		PersonEntity personMock = Mockito.mock(PersonEntity.class);
		personMock.setId(1L);

		Mockito.when(operationMock.getPerson()).thenReturn(personMock);
		Mockito.when(personMock.getId()).thenReturn(1L);

		Mockito.when(operationService.getTotalIngress(Mockito.anyLong())).thenReturn(totalIngress);
		Mockito.when(operationService.getTotalExpenses(Mockito.anyLong())).thenReturn(totalExpenses);
		Mockito.when(operationMock.getAmount()).thenReturn(amountSend);

		operationService.saveSendingMoney(operationMock);

	}

	@Test
	public void testSaveSendingMoney() throws Exception {

		BigDecimal totalIngress = new BigDecimal(2);
		BigDecimal totalExpenses = new BigDecimal(1);
		BigDecimal amountSend = new BigDecimal(0);

		OperationEntity operationMock = Mockito.mock(OperationEntity.class);
		PersonEntity personMock = Mockito.mock(PersonEntity.class);
		personMock.setId(1L);

		Mockito.when(operationMock.getPerson()).thenReturn(personMock);
		Mockito.when(personMock.getId()).thenReturn(1L);

		Mockito.when(operationService.getTotalIngress(Mockito.anyLong())).thenReturn(totalIngress);
		Mockito.when(operationService.getTotalExpenses(Mockito.anyLong())).thenReturn(totalExpenses);
		Mockito.when(operationMock.getAmount()).thenReturn(amountSend);

		operationService.saveSendingMoney(operationMock);
		verify(operationRepositoryMock, Mockito.times(1)).save(Mockito.any(OperationEntity.class));
	}

}
