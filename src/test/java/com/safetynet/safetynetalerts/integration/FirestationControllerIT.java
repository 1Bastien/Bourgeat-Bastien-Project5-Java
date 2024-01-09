package com.safetynet.safetynetalerts.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class FirestationControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testGetListOfPersonByFirestation() throws Exception {
		mockMvc.perform(get("/firestation?stationNumber=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPhoneNumbersByFirestation() throws Exception {
		mockMvc.perform(get("/phoneAlert?firestation=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonsByStations() throws Exception {
		mockMvc.perform(get("/flood/stations?stations=1,2").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

}
