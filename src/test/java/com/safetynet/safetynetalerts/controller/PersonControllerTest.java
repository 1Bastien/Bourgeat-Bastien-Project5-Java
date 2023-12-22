package com.safetynet.safetynetalerts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Test
	public void testGetChildsByAddress() throws Exception {
		List<Map<String, Object>> childList = new ArrayList<>();
		Map<String, Object> child = new HashMap<>();
		child.put("firstName", "John");
		child.put("lastName", "Doe");
		child.put("age", 10);
		childList.add(child);

		List<Map<String, Object>> otherMembers = new ArrayList<>();
		Map<String, Object> otherMember = new HashMap<>();
		otherMember.put("firstName", "Jane");
		otherMember.put("lastName", "Doe");
		otherMembers.add(otherMember);

		child.put("otherMembers", otherMembers);

		childList.add(child);

		when(personService.getChildsByAddress(any(String.class))).thenReturn(childList);

		mockMvc.perform(get("/childAlert?address=123%20Main%20St").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].firstName").value("John"))
				.andExpect(jsonPath("$[0].lastName").value("Doe")).andExpect(jsonPath("$[0].age").value(10))
				.andExpect(jsonPath("$[0].otherMembers[0].firstName").value("Jane"))
				.andExpect(jsonPath("$[0].otherMembers[0].lastName").value("Doe"));
	}

	@Test
	public void testGetPersonsAndFirestationByAddress() throws Exception {
		List<Map<String, Object>> personsAndFirestation = new ArrayList<>();

		Map<String, Object> person = new HashMap<>();
		person.put("firstName", "John");
		person.put("lastName", "Doe");
		person.put("phone", "123-456-7890");
		person.put("age", 30);
		person.put("medications", Arrays.asList("Medication1", "Medication2"));
		person.put("allergies", Arrays.asList("Allergy1", "Allergy2"));

		personsAndFirestation.add(person);

		Map<String, Object> firestation = new HashMap<>();
		firestation.put("firestationNumber", 1);

		personsAndFirestation.add(firestation);

		when(personService.getPersonsAndFirestationByAddress(any(String.class))).thenReturn(personsAndFirestation);

		mockMvc.perform(get("/fire?address=123%20Main%20St").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].firstName").value("John"))
				.andExpect(jsonPath("$[0].lastName").value("Doe"))
				.andExpect(jsonPath("$[0].phone").value("123-456-7890")).andExpect(jsonPath("$[0].age").value(30))
				.andExpect(jsonPath("$[0].medications[0]").value("Medication1"))
				.andExpect(jsonPath("$[0].allergies[0]").value("Allergy1"))
				.andExpect(jsonPath("$[1].firestationNumber").value(1));
	}

	@Test
	public void testGetPersonInfoByFirstNameAndLastName() throws Exception {
		Map<String, Object> personInfo = new HashMap<>();
		personInfo.put("firstName", "John");
		personInfo.put("lastName", "Doe");
		personInfo.put("age", 30);
		personInfo.put("address", "123 Main St");
		personInfo.put("email", "john.doe@example.com");
		personInfo.put("medications", Arrays.asList("Medication1", "Medication2"));
		personInfo.put("allergies", Arrays.asList("Allergy1", "Allergy2"));

		List<Map<String, Object>> personsInfo = new ArrayList<>();

		personsInfo.add(personInfo);

		when(personService.getPersonInfoByFirstNameAndLastName("John", "Doe")).thenReturn(personsInfo);

		mockMvc.perform(get("/personInfo?firstName=John&lastName=Doe").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].firstName").value("John"))
				.andExpect(jsonPath("$[0].lastName").value("Doe")).andExpect(jsonPath("$[0].age").value(30))
				.andExpect(jsonPath("$[0].address").value("123 Main St"))
				.andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
				.andExpect(jsonPath("$[0].medications[0]").value("Medication1"))
				.andExpect(jsonPath("$[0].allergies[0]").value("Allergy1"));
	}

	@Test
	public void testGetCommunityEmails() throws Exception {
		List<String> simulatedEmails = Arrays.asList("john.doe@example.com", "jane.smith@example.com");

		when(personService.getCommunityEmails("Culver")).thenReturn(simulatedEmails);

		mockMvc.perform(get("/communityEmail?city=Culver").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0]").value("john.doe@example.com"))
				.andExpect(jsonPath("$[1]").value("jane.smith@example.com"));
	}

}
