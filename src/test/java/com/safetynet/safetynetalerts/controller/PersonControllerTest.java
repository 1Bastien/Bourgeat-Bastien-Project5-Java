package com.safetynet.safetynetalerts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.DTO.CommunityEmailsDTO;
import com.safetynet.safetynetalerts.DTO.ListChildDTO;
import com.safetynet.safetynetalerts.DTO.PersonInfoDTO;
import com.safetynet.safetynetalerts.DTO.ResidentsAndFirestationDTO;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.service.PersonService;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private PersonService personService;

	@Test
	public void testPostPerson() throws Exception {
		Person newPerson = new Person();
		newPerson.setFirstName("John");
		newPerson.setLastName("Boyd");
		newPerson.setAddress("1509 Culver St");
		newPerson.setCity("Culver");
		newPerson.setZip("97451");
		newPerson.setPhone("841-874-6512");
		newPerson.setEmail("John@test.com");

		when(personService.postPerson(newPerson)).thenReturn(newPerson);

		mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newPerson))).andExpect(status().isCreated());
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

		when(personService.putPerson("John", "Boyd", newPerson)).thenReturn(newPerson);

		mockMvc.perform(put("/person/John/Boyd").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newPerson))).andExpect(status().isOk());
	}

	@Test
	public void testDeletePerson() throws Exception {
		when(personService.deletePerson("John", "Boyd")).thenReturn("Person deleted");

		mockMvc.perform(delete("/person/John/Boyd").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString("Person deleted"))).andExpect(status().isOk());
	}

	@Test
	public void testGetChildsByAddress() throws Exception {
		ListChildDTO listChildDTO = new ListChildDTO();

		when(personService.getChildsByAddress("123 Main St")).thenReturn(listChildDTO);

		mockMvc.perform(get("/childAlert?address=123%20Main%20St").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(listChildDTO))).andExpect(status().isOk());
	}

	@Test
	public void testGetPersonsAndFirestationByAddress() throws Exception {
		ResidentsAndFirestationDTO personsAndFirestation = new ResidentsAndFirestationDTO();

		when(personService.getResidentsAndFirestationByAddress(any(String.class))).thenReturn(personsAndFirestation);

		mockMvc.perform(get("/fire?address=123%20Main%20St").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(personsAndFirestation))).andExpect(status().isOk());
	}

	@Test
	public void testGetPersonInfoByFirstNameAndLastName() throws Exception {
		PersonInfoDTO personInfo = new PersonInfoDTO();

		when(personService.getPersonInfoByFirstNameAndLastName("John", "Doe")).thenReturn(personInfo);

		mockMvc.perform(get("/personInfo?firstName=John&lastName=Doe").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(personInfo))).andExpect(status().isOk());
	}

	@Test
	public void testGetCommunityEmails() throws Exception {
		CommunityEmailsDTO listEmails = new CommunityEmailsDTO();

		when(personService.getCommunityEmails("Culver")).thenReturn(listEmails);

		mockMvc.perform(get("/communityEmail?city=Culver").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(listEmails))).andExpect(status().isOk());
	}

}
