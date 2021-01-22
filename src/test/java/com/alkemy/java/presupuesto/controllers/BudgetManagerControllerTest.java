package com.alkemy.java.presupuesto.controllers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.context.WebApplicationContext;

import com.alkemy.java.budgetManager.Utils.PersonAuthenticationUtil;
import com.alkemy.java.budgetManager.controllers.BudgetManagerController;
import com.alkemy.java.budgetManager.entities.OperationEntity;
import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.models.Type;
import com.alkemy.java.budgetManager.service.IOperationService;
import com.alkemy.java.budgetManager.service.IPersonService;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebAppConfiguration
public class BudgetManagerControllerTest {

	@Mock
	private IPersonService personServiceMock;

	@Mock
	private IOperationService operationServiceMock;

	@Mock
	private PersonAuthenticationUtil personAuthenticationUtilMock;

	@InjectMocks
	private BudgetManagerController budgetManagerController;

	private MockMvc mockMvc;

	@SuppressWarnings("deprecation")
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(budgetManagerController).build();

	}

	@Test
	public void testHomePerson() throws Exception {

		PersonEntity personUser = Mockito.mock(PersonEntity.class);
		personUser.setId(1L);
		Mockito.when(personUser.getId()).thenReturn(1L);

		Mockito.when(personAuthenticationUtilMock.personAuthentication()).thenReturn(personUser);

		List<OperationEntity> listOperationsPerson = new ArrayList<OperationEntity>();

		OperationEntity op1 = new OperationEntity();
		OperationEntity op2 = new OperationEntity();

		op1.setId(1L);
		op1.setConcept("Pago Alquiler");
		op1.setAmount(new BigDecimal(2000));
		op1.setType(Type.EXPENSES);

		op2.setId(2L);
		op2.setConcept("Venta de Bicicleta");
		op2.setAmount(new BigDecimal(10000));
		op2.setType(Type.INGRESS);

		listOperationsPerson.add(op1);
		listOperationsPerson.add(op2);

		when(operationServiceMock.getLastTenOperation(Mockito.anyLong())).thenReturn(listOperationsPerson);
		when(operationServiceMock.getCurrentBalance(Mockito.anyLong())).thenReturn(new BigDecimal(8000));

		mockMvc.perform(get("/operation/person/")).andExpect(status().isOk())
				.andExpect(view().name("budgetManager/homeOperationPerson"))
				.andExpect(model().attribute("operations", hasSize(2)))
				.andExpect(model().attribute("operations",
						hasItem(allOf(hasProperty("id", is(1L)), hasProperty("concept", is("Pago Alquiler")),
								hasProperty("amount", is(new BigDecimal(2000))),
								hasProperty("type", is(Type.EXPENSES))))))
				.andExpect(model().attribute("operations",
						hasItem(allOf(hasProperty("id", is(2L)), hasProperty("concept", is("Venta de Bicicleta")),
								hasProperty("amount", is(new BigDecimal(10000))),
								hasProperty("type", is(Type.INGRESS))))))
				.andExpect(model().attribute("balance", new BigDecimal(8000)))
				.andExpect(model().attribute("person", instanceOf(PersonEntity.class)));

		verify(operationServiceMock, times(1)).getLastTenOperation(Mockito.anyLong());
		verify(operationServiceMock, times(1)).getCurrentBalance(Mockito.anyLong());
		verifyNoMoreInteractions(operationServiceMock);

	}

	@Test
	public void testAddOperation() throws Exception {

		PersonEntity personUser = Mockito.mock(PersonEntity.class);
		personUser.setId(1L);

		Mockito.when(personAuthenticationUtilMock.personAuthentication()).thenReturn(personUser);

		mockMvc.perform(get("/operation/addOperation/")).andExpect(status().isOk())
				.andExpect(view().name("budgetManager/addOperation")).andExpect(model().attribute("person", personUser))
				.andExpect(model().attribute("operation", instanceOf(OperationEntity.class)))
				.andExpect(model().attribute("operation", hasProperty("person", instanceOf(PersonEntity.class))));

		verifyNoInteractions(operationServiceMock);

	}

	@Test
	public void test1SaveOperationValidationFailsIdNull() throws Exception {

		String concept = "";

		mockMvc.perform(post("/operation/saveOperation").param("concept", concept).param("amount", "400").param("type",
				"EXPENSES")).andExpect(status().isOk()).andExpect(view().name("budgetManager/addOperation"))
				.andExpect(model().attribute("operation", hasProperty("id", nullValue())))
				.andExpect(model().attribute("operation", instanceOf(OperationEntity.class)))
				.andExpect(model().attribute("operation", hasProperty("concept", is(""))))
				.andExpect(model().attribute("operation", hasProperty("amount", is(new BigDecimal("400")))))
				.andExpect(model().attribute("operation", hasProperty("type", is(Type.EXPENSES))));

		verifyNoInteractions(operationServiceMock);
	}

	@Test
	public void test2SaveOperationValidationFailsIdNotnull() throws Exception {

		Long id = 1L;
		String concept = "";

		mockMvc.perform(post("/operation/saveOperation").param("id", "1").param("concept", concept)
				.param("amount", "400").param("type", "EXPENSES")).andExpect(status().isOk())
				.andExpect(view().name("budgetManager/addOperation"))
				.andExpect(model().attribute("operation", hasProperty("id", is(id))))
				.andExpect(model().attribute("operation", instanceOf(OperationEntity.class)))
				.andExpect(model().attribute("operation", hasProperty("concept", is(""))))
				.andExpect(model().attribute("operation", hasProperty("amount", is(new BigDecimal(400)))))
				.andExpect(model().attribute("operation", hasProperty("type", is(Type.EXPENSES))));

		verifyNoInteractions(operationServiceMock);
	}

	@Test
	public void test3SaveOperationEntryIsUpdatedToTheDatabase() throws Exception {

		mockMvc.perform(post("/operation/saveOperation").param("concept", "Pago Alquiler").param("amount", "400")
				.param("type", "EXPENSES")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/operation/person/"));

		ArgumentCaptor<OperationEntity> formObjectArgument = ArgumentCaptor.forClass(OperationEntity.class);
		verify(operationServiceMock).saveOperation(formObjectArgument.capture());

		OperationEntity formObject = formObjectArgument.getValue();

		assertEquals(formObject.getConcept(), "Pago Alquiler");
		assertEquals(formObject.getAmount(), new BigDecimal(400));
		assertEquals(formObject.getType(), Type.EXPENSES);
		assertNull(formObject.getId());

		verify(operationServiceMock, times(1)).saveOperation(formObject);
		verifyNoMoreInteractions(operationServiceMock);

	}

}
