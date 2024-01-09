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
import com.safetynet.safetynetalerts.DTO.CommunityEmailsDTO;
import com.safetynet.safetynetalerts.DTO.ListChildDTO;
import com.safetynet.safetynetalerts.DTO.PersonInfoDTO;
import com.safetynet.safetynetalerts.DTO.ResidentsAndFirestationDTO;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	public void testGetChildsByAddress() throws Exception {
		MvcResult result = mockMvc
				.perform(get("/childAlert").param("address", "1509 Culver St").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		ListChildDTO listChildDTO = objectMapper.readValue(result.getResponse().getContentAsString(),
				ListChildDTO.class);

		assertNotNull(listChildDTO);
	}

	@Test
	public void testGetPersonsAndFirestationByAddress() throws Exception {
		MvcResult result = mockMvc
				.perform(get("/fire").param("address", "1509 Culver St").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		ResidentsAndFirestationDTO residentsAndFirestationDTO = objectMapper
				.readValue(result.getResponse().getContentAsString(), ResidentsAndFirestationDTO.class);

		assertNotNull(residentsAndFirestationDTO);
	}

	@Test
	public void testGetPersonInfoByFirstNameAndLastName() throws Exception {
		MvcResult result = mockMvc.perform(get("/personInfo").param("firstName", "John").param("lastName", "Boyd")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		PersonInfoDTO personInfoDTO = objectMapper.readValue(result.getResponse().getContentAsString(),
				PersonInfoDTO.class);

		assertNotNull(personInfoDTO);
	}

	@Test
	public void testGetCommunityEmails() throws Exception {
		MvcResult result = mockMvc
				.perform(get("/communityEmail").param("city", "Culver").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		CommunityEmailsDTO communityEmailsDTO = objectMapper.readValue(result.getResponse().getContentAsString(),
				CommunityEmailsDTO.class);

		assertNotNull(communityEmailsDTO);
	}

}