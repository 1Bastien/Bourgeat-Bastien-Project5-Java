package com.safetynet.safetynetalerts.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.DTO.FirestationInfoDTO;
import com.safetynet.safetynetalerts.DTO.PersonsByStationsDTO;
import com.safetynet.safetynetalerts.DTO.PhoneNumbersDTO;

@SpringBootTest
@AutoConfigureMockMvc
public class FirestationControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	public void testGetListOfPersonByFirestation() throws Exception {
		MvcResult result = mockMvc.perform(get("/firestation?stationNumber=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		FirestationInfoDTO firestationInfoDTO = objectMapper.readValue(result.getResponse().getContentAsString(),
				FirestationInfoDTO.class);

		assertNotNull(firestationInfoDTO);
	}

	@Test
	public void testGetPhoneNumbersByFirestation() throws Exception {
		MvcResult result = mockMvc.perform(get("/phoneAlert?firestation=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		PhoneNumbersDTO phoneNumbersDTO = objectMapper.readValue(result.getResponse().getContentAsString(),
				PhoneNumbersDTO.class);

		assertNotNull(phoneNumbersDTO);
	}

	@Test
	public void testGetPersonsByStations() throws Exception {
		MvcResult result = mockMvc.perform(get("/flood/stations?stations=1,2").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		PersonsByStationsDTO personsByStationsDTO = objectMapper.readValue(result.getResponse().getContentAsString(),
				PersonsByStationsDTO.class);

		assertNotNull(personsByStationsDTO);
	}

}
