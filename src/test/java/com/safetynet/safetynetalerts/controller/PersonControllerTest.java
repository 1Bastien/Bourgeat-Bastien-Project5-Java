package com.safetynet.safetynetalerts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.service.PersonService;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PersonService personService;

	@Test
	public void testPostPerson() throws Exception {
		when(personService.postPerson(any(Person.class))).thenReturn(any(Person.class));

		mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON).content(
				"{\"firstName\":\"John\",\"lastName\":\"Boyd\",\"address\":\"1509 Culver St\",\"city\":\"Culver\",\"zip\":\"97451\",\"phone\":\"841-874-6512\",\"email\":\"jaboyd@email.com\"}"))
				.andExpect(status().isOk()).andReturn();
	}

	@Test
	public void testPutPerson() throws Exception {
		Person newPerson = new Person();
		newPerson.setFirstName("John");
		newPerson.setLastName("Boyd");
		newPerson.setAddress("1509 Culver St");
		newPerson.setCity("Culver");
		newPerson.setZip("97451");
		newPerson.setPhone("841-874-6512");
		newPerson.setEmail("new.email@example.com");

		when(personService.putPerson(eq("John"), eq("Boyd"), eq(newPerson))).thenReturn(newPerson);

		mockMvc.perform(put("/person/John/Boyd").contentType(MediaType.APPLICATION_JSON).content(
				"{\"firstName\":\"John\",\"lastName\":\"Boyd\",\"address\":\"1509 Culver St\",\"city\":\"Culver\",\"zip\":\"97451\",\"phone\":\"841-874-6512\",\"email\":\"jaboyd@email.com\"}"))
				.andExpect(status().isOk()).andReturn();
	}

	@Test
	public void testDeletePerson() throws Exception {
		when(personService.deletePerson(eq("John"), eq("Boyd"))).thenReturn("Person deleted");
		
		mockMvc.perform(delete("/person/John/Boyd")).andExpect(status().isOk()).andExpect(content().string("Person deleted")).andReturn();
	}

}
