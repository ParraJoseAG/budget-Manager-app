package com.alkemy.java.presupuesto.controllers;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

import com.alkemy.java.budgetManager.controllers.LoginController;
import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.service.IPersonService;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebAppConfiguration
public class LoginControllerTest {

	@Mock
	private IPersonService personServiceMock;

	@InjectMocks
	private LoginController loginController;

	private MockMvc mockMvc;

	@SuppressWarnings("deprecation")
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();

	}

	@Test
	public void testregisterForm() throws Exception {

		mockMvc.perform(get("/auth/register/")).andExpect(status().isOk()).andExpect(view().name("/login/register"))
				.andExpect(model().attribute("person", instanceOf(PersonEntity.class)));

		verifyNoInteractions(personServiceMock);
	}

	@Test
	public void test1SavePersonValidationFailsIdNull() throws Exception {

		String name = "";
		String surname = "";
		String username = "";
		String email = "";
		String password = "";

		mockMvc.perform(post("/auth/saveRegister").param("name", name).param("surname", surname)
				.param("username", username).param("email", email).param("password", password))
				.andExpect(status().isOk()).andExpect(view().name("/login/register"))
				.andExpect(model().attribute("person", hasProperty("id", nullValue())))
				.andExpect(model().attribute("person", instanceOf(PersonEntity.class)))
				.andExpect(model().attribute("person", hasProperty("name", is(""))))
				.andExpect(model().attribute("person", hasProperty("surname", is(""))))
				.andExpect(model().attribute("person", hasProperty("username", is(""))))
				.andExpect(model().attribute("person", hasProperty("email", is(""))))
				.andExpect(model().attribute("person", hasProperty("password", is(""))));

		verifyNoInteractions(personServiceMock);
	}

	@Test
	public void test2SavePersonValidationFailsIdNotNull() throws Exception {

		Long id = 1L;
		String name = "";
		String surname = "";
		String username = "";
		String email = "";
		String password = "";

		mockMvc.perform(post("/auth/saveRegister").param("id", "1").param("name", name).param("surname", surname)
				.param("username", username).param("email", email).param("password", password))
				.andExpect(status().isOk()).andExpect(view().name("/login/register"))
				.andExpect(model().attribute("person", hasProperty("id", is(id))))
				.andExpect(model().attribute("person", instanceOf(PersonEntity.class)))
				.andExpect(model().attribute("person", hasProperty("name", is(""))))
				.andExpect(model().attribute("person", hasProperty("surname", is(""))))
				.andExpect(model().attribute("person", hasProperty("username", is(""))))
				.andExpect(model().attribute("person", hasProperty("email", is(""))))
				.andExpect(model().attribute("person", hasProperty("password", is(""))));

		verifyNoInteractions(personServiceMock);
	}

	@Test
	public void test3SavePersonEntryIsUpdatedToTheDatabase() throws Exception {

		String name = "Jose Guillermo";
		String surname = "Parra Aponte";
		String username = "PARRAJOSEAG1502";
		String email = "jgparra1603@gmail.com";
		String password = "1234";

		when(personServiceMock.findByUsername(Mockito.anyString())).thenReturn(null);
		when(personServiceMock.findByEmail(Mockito.anyString())).thenReturn(null);

		mockMvc.perform(post("/auth/saveRegister").param("name", name).param("surname", surname)
				.param("username", username).param("email", email).param("password", password))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/"));

		ArgumentCaptor<PersonEntity> formObjectArgument = ArgumentCaptor.forClass(PersonEntity.class);
		verify(personServiceMock).savePerson(formObjectArgument.capture());

		PersonEntity formObject = formObjectArgument.getValue();

		assertEquals(formObject.getName(), name);
		assertEquals(formObject.getSurname(), surname);
		assertEquals(formObject.getUsername(), username);
		assertEquals(formObject.getEmail(), email);
		assertEquals(formObject.getPassword(), password);
		assertNull(formObject.getId());

	}

	@Test
	public void test4SavePersonValidationFailsPattern() throws Exception {

		Long id = 1L;
		String name = "Jose Guillermo1603";
		String surname = "Parra Aponte";
		String username = "PARRAJOSEAG";
		String email = "jgparra1603@gmail.com";
		String password = "1234";

		mockMvc.perform(post("/auth/saveRegister").param("id", "1").param("name", name).param("surname", surname)
				.param("username", username).param("email", email).param("password", password))
				.andExpect(status().isOk()).andExpect(view().name("/login/register"))
				.andExpect(model().attribute("person", hasProperty("id", is(id))))
				.andExpect(model().attribute("person", instanceOf(PersonEntity.class)))
				.andExpect(model().attribute("person", hasProperty("name", is("Jose Guillermo1603"))))
				.andExpect(model().attribute("person", hasProperty("surname", is("Parra Aponte"))))
				.andExpect(model().attribute("person", hasProperty("username", is("PARRAJOSEAG"))))
				.andExpect(model().attribute("person", hasProperty("email", is("jgparra1603@gmail.com"))))
				.andExpect(model().attribute("person", hasProperty("password", is("1234"))));

		verifyNoInteractions(personServiceMock);
	}

	@Test
	public void test5SavePersonfindByUsernameNotNull() throws Exception {

		PersonEntity person = new PersonEntity();

		Long id = 1L;
		String name = "Jose Guillermo";
		String surname = "Parra Aponte";
		String username = "PARRAJOSEAG";
		String email = "jgparra1603@gmail.com";
		String password = "1234";

		person.setName(name);
		person.setSurname(surname);
		person.setUsername(username);
		person.setEmail(email);
		person.setPassword(password);

		when(personServiceMock.findByUsername(Mockito.anyString())).thenReturn(person);

		mockMvc.perform(post("/auth/saveRegister").param("id", "1").param("name", name).param("surname", surname)
				.param("username", username).param("email", email).param("password", password))
				.andExpect(status().isOk()).andExpect(view().name("/login/register"))
				.andExpect(model().attribute("person", hasProperty("id", is(id))))
				.andExpect(model().attribute("person", instanceOf(PersonEntity.class)))
				.andExpect(model().attribute("person", hasProperty("name", is("Jose Guillermo"))))
				.andExpect(model().attribute("person", hasProperty("surname", is("Parra Aponte"))))
				.andExpect(model().attribute("person", hasProperty("username", is("PARRAJOSEAG"))))
				.andExpect(model().attribute("person", hasProperty("email", is("jgparra1603@gmail.com"))))
				.andExpect(model().attribute("person", hasProperty("password", is("1234"))))
				.andExpect(model().attribute("message", "Ya existe un usuario con el nombre de usuario dado."));

	}

	@Test
	public void test6SavePersonfindByEmailNotNull() throws Exception {

		PersonEntity person = new PersonEntity();

		Long id = 1L;
		String name = "Jose Guillermo";
		String surname = "Parra Aponte";
		String username = "PARRAJOSEAG";
		String email = "jgparra1603@gmail.com";
		String password = "1234";

		person.setName(name);
		person.setSurname(surname);
		person.setUsername(username);
		person.setEmail(email);
		person.setPassword(password);

		when(personServiceMock.findByEmail(Mockito.anyString())).thenReturn(person);

		mockMvc.perform(post("/auth/saveRegister").param("id", "1").param("name", name).param("surname", surname)
				.param("username", username).param("email", email).param("password", password))
				.andExpect(status().isOk()).andExpect(view().name("/login/register"))
				.andExpect(model().attribute("person", hasProperty("id", is(id))))
				.andExpect(model().attribute("person", instanceOf(PersonEntity.class)))
				.andExpect(model().attribute("person", hasProperty("name", is("Jose Guillermo"))))
				.andExpect(model().attribute("person", hasProperty("surname", is("Parra Aponte"))))
				.andExpect(model().attribute("person", hasProperty("username", is("PARRAJOSEAG"))))
				.andExpect(model().attribute("person", hasProperty("email", is("jgparra1603@gmail.com"))))
				.andExpect(model().attribute("person", hasProperty("password", is("1234"))))
				.andExpect(model().attribute("message", "Ya existe un usuario con la dirección de correo dada."));

	}

}
