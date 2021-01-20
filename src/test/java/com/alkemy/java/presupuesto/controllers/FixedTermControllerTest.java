package com.alkemy.java.presupuesto.controllers;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.alkemy.java.budgetManager.Utils.FixedTermUtil;
import com.alkemy.java.budgetManager.controllers.FixedTermController;
import com.alkemy.java.budgetManager.entities.FixedTermEntity;
import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.exceptions.FixedTermNotFoundException;
import com.alkemy.java.budgetManager.service.IFixedTermService;
import com.alkemy.java.budgetManager.service.IOperationService;
import com.alkemy.java.budgetManager.service.IPersonService;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebAppConfiguration
public class FixedTermControllerTest {

	@Mock
	private IPersonService personServiceMock;

	@Mock
	private IFixedTermService fixedTermServiceMock;

	@Mock
	private IOperationService operationServiceMock;

	@Mock
	private FixedTermUtil fixedTermUtilMock;

	@InjectMocks
	private FixedTermController fixedTermController;

	private MockMvc mockMvc;

	@SuppressWarnings("deprecation")
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(fixedTermController).build();

	}

	@Test
	public void test1DepositsFixed() throws Exception {

		List<FixedTermEntity> listDepositsFixed = new ArrayList<FixedTermEntity>();

		FixedTermEntity fixedTerm1 = new FixedTermEntity();
		FixedTermEntity fixedTerm2 = new FixedTermEntity();

		fixedTerm1.setId(1L);
		fixedTerm1.setAmountFixedTerm(new BigDecimal(100));
		fixedTerm1.setStartDate(new Date());

		fixedTerm2.setId(2L);
		fixedTerm2.setAmountFixedTerm(new BigDecimal(200));
		fixedTerm2.setStartDate(new Date());

		listDepositsFixed.add(fixedTerm1);
		listDepositsFixed.add(fixedTerm2);

		Mockito.when(personServiceMock.getPersonById(Mockito.anyLong())).thenReturn(new PersonEntity());
		Mockito.when(fixedTermServiceMock.getListFixedTermPerson(Mockito.anyLong())).thenReturn(listDepositsFixed);

		Mockito.when(operationServiceMock.getCurrentBalance(Mockito.anyLong())).thenReturn(new BigDecimal(400));

		mockMvc.perform(get("/fixedTerm/person/{id}", 1L)).andExpect(status().isOk())
				.andExpect(view().name("fixedTerm/depositsFixed"))
				.andExpect(model().attribute("listDepositsFixed", hasSize(2)))
				.andExpect(model().attribute("listDepositsFixed",
						hasItem(allOf(hasProperty("id", is(1L)),
								hasProperty("amountFixedTerm", is(new BigDecimal(100)))))))
				.andExpect(model().attribute("listDepositsFixed",
						hasItem(allOf(hasProperty("id", is(2L)),
								hasProperty("amountFixedTerm", is(new BigDecimal(200)))))))
				.andExpect(model().attribute("balance", new BigDecimal(400)))
				.andExpect(model().attribute("person", instanceOf(PersonEntity.class)));

	}

	@Test(expected = Exception.class)
	public void test1SaveFixedTermThrowException() throws Exception {

		FixedTermEntity fixedTerm = Mockito.mock(FixedTermEntity.class);
		Mockito.doThrow().when(fixedTermServiceMock).saveFixedTerm(fixedTerm);

		mockMvc.perform(post("/fixedTerm/person/saveFixedTerm")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/fixedTerm/person/")).andExpect(flash().attribute("error",
						"No tiene suficiente dinero disponible en su balance para realizar la operación!"));

	}

	@Test
	public void test2SaveFixedTerm() throws Exception {

		mockMvc.perform(
				post("/fixedTerm/person/saveFixedTerm").param("amountFixedTerm", "500").param("amountGenerated", "0"))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/fixedTerm/person/"))
				.andExpect(flash().attribute("success", "Operación exitosa!"));

		ArgumentCaptor<FixedTermEntity> formObjectArgument = ArgumentCaptor.forClass(FixedTermEntity.class);
		verify(fixedTermServiceMock).saveFixedTerm(formObjectArgument.capture());

		FixedTermEntity fixedTerm = formObjectArgument.getValue();

		assertEquals(fixedTerm.getAmountFixedTerm(), new BigDecimal(500));
		assertEquals(fixedTerm.getAmountGenerated(), new BigDecimal(0));
		assertNull(fixedTerm.getId());

	}

	@Test
	public void test1endFixedTermThrowFixedTermNotFoundException() throws Exception {

		Mockito.when(fixedTermServiceMock.getByIdFixedTerm(Mockito.anyLong()))
				.thenThrow(new FixedTermNotFoundException("Plazo fijo no existe"));

		mockMvc.perform(get("/fixedTerm/person/endFixedTerm/{id}", 1L)).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/fixedTerm/person/"))
				.andExpect(flash().attribute("error", "Plazo fijo no existe"));

		verify(fixedTermServiceMock, times(1)).getByIdFixedTerm(Mockito.anyLong());

	}

	@Test
	public void test2endFixedTerm() throws Exception {

		Mockito.when(fixedTermServiceMock.getByIdFixedTerm(Mockito.anyLong())).thenReturn(new FixedTermEntity());

		mockMvc.perform(get("/fixedTerm/person/endFixedTerm/{id}", 1L)).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/fixedTerm/person/"))
				.andExpect(flash().attribute("success", "Deposito en cuenta por plazo fijo exitoso!"));

		verify(fixedTermServiceMock, times(1)).deleteByIdFixedTerm(Mockito.anyLong());
		verify(fixedTermServiceMock, times(1)).getByIdFixedTerm(Mockito.anyLong());

	}

}
