package com.safetynet.safetynetalerts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.service.FirestationService;

@WebMvcTest(FirestationController.class)
public class FirestationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FirestationService firestationService;

	@Test
	public void testPostFirestations() throws Exception {
		when(firestationService.postFirestation(any(Firestation.class))).thenReturn(any(Firestation.class));
		
		mockMvc.perform(post("/firestation").contentType(MediaType.APPLICATION_JSON).content(
                "{\"address\":\"1509 Culver St\",\"station\":\"3\"}"))
                .andExpect(status().isOk()).andReturn();
	}

	@Test
	public void testPutFirestations() throws Exception {
		Firestation newFirestation = new Firestation();
		newFirestation.setAddress("1509 Culver St");
		newFirestation.setStation(3);

		when(firestationService.putFirestation("1509 Culver St", newFirestation)).thenReturn(newFirestation);

		mockMvc.perform(put("/firestation/1509 Culver St").contentType(MediaType.APPLICATION_JSON)
				.content("{\"address\":\"1509 Culver St\",\"station\":\"3\"}")).andExpect(status().isOk()).andReturn();
	}

	@Test
	public void testDeleteFirestations() throws Exception {
        when(firestationService.deleteFirestation("1509 Culver St")).thenReturn("Firestation object deleted");
        
        mockMvc.perform(delete("/firestation/1509 Culver St").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(content().string("Firestation object deleted")).andReturn();
    }
}
