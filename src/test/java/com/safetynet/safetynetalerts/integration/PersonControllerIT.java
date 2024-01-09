package com.safetynet.safetynetalerts.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testGetChildsByAddress() throws Exception {
		String address = "123 Main St";

		mockMvc.perform(get("/childAlert").param("address", address).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	public void testGetPersonsAndFirestationByAddress() throws Exception {
		mockMvc.perform(get("/fire").param("address", "1509 Culver St").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonInfoByFirstNameAndLastName() throws Exception {
		mockMvc.perform(get("/personInfo").param("firstName", "John").param("lastName", "Boyd")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void testGetCommunityEmails() throws Exception {
		mockMvc.perform(get("/communityEmail").param("city", "Culver").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}