package com.safetynet.safetynetalerts.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.DTO.FirestationInfoDTO;
import com.safetynet.safetynetalerts.DTO.PersonsByStationsDTO;
import com.safetynet.safetynetalerts.DTO.PhoneNumbersDTO;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.service.FirestationService;

@WebMvcTest(FirestationController.class)
public class FirestationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private FirestationService firestationService;

	@Test
	public void testPostFirestations() throws Exception {
		Firestation newFirestation = new Firestation();
		newFirestation.setAddress("1509 Culver St");
		newFirestation.setStation(3);

		when(firestationService.postFirestation(newFirestation)).thenReturn(newFirestation);

		mockMvc.perform(post("/firestation").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newFirestation))).andExpect(status().isCreated());
	}

	@Test
	public void testPutFirestations() throws Exception {
		Firestation newFirestation = new Firestation();
		newFirestation.setAddress("1509 Culver St");
		newFirestation.setStation(3);

		when(firestationService.putFirestation("1509 Culver St", newFirestation)).thenReturn(newFirestation);

		mockMvc.perform(put("/firestation/1509%20Culver%20St").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newFirestation))).andExpect(status().isOk());
	}

	@Test
	public void testDeleteFirestations() throws Exception {
		when(firestationService.deleteFirestation("1509 Culver St")).thenReturn("Firestation object deleted");

		mockMvc.perform(delete("/firestation/1509%20Culver%20St").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString("Firestation object deleted"))).andExpect(status().isOk());
	}

	@Test
	public void testGetListOfPersonByFirestation() throws Exception {
		FirestationInfoDTO firestationInfo = new FirestationInfoDTO();

		when(firestationService.getListOfPersonByFirestation(1)).thenReturn(firestationInfo);

		mockMvc.perform(get("/firestation?stationNumber=1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(firestationInfo))).andExpect(status().isOk());
	}

	@Test
	public void testGetPhoneNumbersByFirestation() throws Exception {
		PhoneNumbersDTO phoneNumbers = new PhoneNumbersDTO();

		when(firestationService.getPhoneNumbersByFirestation(1)).thenReturn(phoneNumbers);

		mockMvc.perform(get("/phoneAlert?firestation=1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(phoneNumbers))).andExpect(status().isOk());
	}

	@Test
	public void testGetPersonsByStations() throws Exception {
		PersonsByStationsDTO persons = new PersonsByStationsDTO();

		when(firestationService.getPersonsByStations(Arrays.asList(1, 2))).thenReturn(persons);

		mockMvc.perform(get("/flood/stations?stations=1,2").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(persons))).andExpect(status().isOk());
	}
}
