package com.alkemy.java.presupuesto.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.alkemy.java.budgetManager.entities.FixedTermEntity;
import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.exceptions.FixedTermNotFoundException;
import com.alkemy.java.budgetManager.repository.IFixedTermRepository;
import com.alkemy.java.budgetManager.service.IOperationService;
import com.alkemy.java.budgetManager.serviceImpl.FixedTermServiceImpl;

public class FixedTermServiceImplTest {

	@Mock
	private IFixedTermRepository fixedTermRepositoryMock;

	@Mock
	private IOperationService operationServiceMock;

	@InjectMocks
	private FixedTermServiceImpl fixedTermServiceImpl;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void tesGetListFixedTermPerson() {

		List<FixedTermEntity> listFixedTerm = new ArrayList<FixedTermEntity>();

		FixedTermEntity fixedTerm1 = new FixedTermEntity();
		fixedTerm1.setId(1L);
		fixedTerm1.setAmountFixedTerm(new BigDecimal(200));

		FixedTermEntity fixedTerm2 = new FixedTermEntity();
		fixedTerm2.setId(2L);
		fixedTerm2.setAmountFixedTerm(new BigDecimal(500));

		FixedTermEntity fixedTerm3 = new FixedTermEntity();
		fixedTerm3.setId(3L);
		fixedTerm3.setAmountFixedTerm(new BigDecimal(1000));

		listFixedTerm.add(fixedTerm1);
		listFixedTerm.add(fixedTerm2);
		listFixedTerm.add(fixedTerm3);

		Mockito.when(fixedTermRepositoryMock.findAllFixedTerm(Mockito.anyLong())).thenReturn(listFixedTerm);

		List<FixedTermEntity> expected = fixedTermServiceImpl.getListFixedTermPerson(Mockito.anyLong());

		assertEquals(expected, listFixedTerm);
		assertEquals(expected.size(), listFixedTerm.size());
		assertEquals(expected.size(), 3);

		verify(fixedTermRepositoryMock, times(1)).findAllFixedTerm(Mockito.anyLong());
	}

	@Test(expected = Exception.class)
	public void testSaveFixedTermShouldThrowsExceptionBalanceMinorFixedTerm() throws Exception {

		BigDecimal balance = new BigDecimal(0);
		BigDecimal amountFixedTerm = new BigDecimal(1);

		FixedTermEntity fixedTermMock = Mockito.mock(FixedTermEntity.class);
		PersonEntity person = Mockito.mock(PersonEntity.class);
		person.setId(1L);

		Mockito.when(fixedTermMock.getPerson()).thenReturn(person);
		Mockito.when(person.getId()).thenReturn(1L);
		Mockito.when(operationServiceMock.getCurrentBalance(Mockito.anyLong())).thenReturn(balance);
		Mockito.when(fixedTermMock.getAmountFixedTerm()).thenReturn(amountFixedTerm);

		fixedTermServiceImpl.saveFixedTerm(fixedTermMock);

		verify(fixedTermRepositoryMock, never()).save(Mockito.any(FixedTermEntity.class));

	}

	@Test
	public void testSaveFixedTermSuccessfully() throws Exception {

		BigDecimal balance = new BigDecimal(1);
		BigDecimal amountFixedTerm = new BigDecimal(0);

		FixedTermEntity fixedTermMock = Mockito.mock(FixedTermEntity.class);
		PersonEntity person = Mockito.mock(PersonEntity.class);
		person.setId(1L);

		Mockito.when(fixedTermMock.getPerson()).thenReturn(person);
		Mockito.when(person.getId()).thenReturn(1L);
		Mockito.when(operationServiceMock.getCurrentBalance(Mockito.anyLong())).thenReturn(balance);
		Mockito.when(fixedTermMock.getAmountFixedTerm()).thenReturn(amountFixedTerm);

		fixedTermServiceImpl.saveFixedTerm(fixedTermMock);

		verify(fixedTermRepositoryMock, Mockito.times(1)).save(Mockito.any(FixedTermEntity.class));

	}

	@Test(expected = FixedTermNotFoundException.class)
	public void testGetByIdFixedTermShouldThrowsFixedTermNotFoundException() {

		Mockito.when(fixedTermRepositoryMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());

		fixedTermServiceImpl.getByIdFixedTerm(Mockito.anyLong());
		verify(fixedTermRepositoryMock, Mockito.times(1)).findById(Mockito.anyLong());
	}

	@Test
	public void testGetByIdFixedTermTermSuccessfully() {

		FixedTermEntity fixedTerm = new FixedTermEntity();
		fixedTerm.setId(1l);

		Mockito.when(fixedTermRepositoryMock.findById(Mockito.anyLong())).thenReturn(Optional.of(fixedTerm));

		FixedTermEntity fixedTermExpected = fixedTermServiceImpl.getByIdFixedTerm(Mockito.anyLong());

		assertEquals(fixedTermExpected, fixedTerm);
		assertThat(fixedTermExpected).isNotNull();

		verify(fixedTermRepositoryMock, Mockito.times(1)).findById(Mockito.anyLong());
	}

	@Test(expected = FixedTermNotFoundException.class)
	public void testDeleteByIdFixedTermShouldThrowsFixedTermNotFoundException() {

		Mockito.when(fixedTermRepositoryMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());

		fixedTermServiceImpl.deleteByIdFixedTerm(Mockito.anyLong());

		verify(fixedTermRepositoryMock, Mockito.times(1)).findById(Mockito.anyLong());
		verify(fixedTermRepositoryMock, Mockito.never()).deleteById(Mockito.anyLong());
	}

	@Test
	public void testDeleteByIdFixedTermSuccessfully() {

		FixedTermEntity fixedTerm = new FixedTermEntity();
		fixedTerm.setId(1l);

		Mockito.when(fixedTermRepositoryMock.findById(Mockito.anyLong())).thenReturn(Optional.of(fixedTerm));

		fixedTermServiceImpl.deleteByIdFixedTerm(Mockito.anyLong());

		verify(fixedTermRepositoryMock, Mockito.times(1)).findById(Mockito.anyLong());
		verify(fixedTermRepositoryMock, Mockito.times(1)).deleteById(Mockito.anyLong());

	}
}
