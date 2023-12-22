package com.safetynet.safetynetalerts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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

	@Test
	public void testGetListOfPersonByFirestation() throws Exception {
		List<Map<String, Object>> personList = new ArrayList<>();

		Map<String, Object> personMap = new HashMap<>();
		personMap.put("firstName", "John");
		personMap.put("lastName", "Boyd");
		personMap.put("address", "1509 Culver St");
		personMap.put("phone", "841-874-6512");

		personList.add(personMap);

		Map<String, Object> adultChildCount = new HashMap<>();
		adultChildCount.put("adultCount", 1);
		adultChildCount.put("childCount", 0);

		personList.add(adultChildCount);

		when(firestationService.getListOfPersonByFirestation(1)).thenReturn(personList);

		mockMvc.perform(get("/firestation?stationNumber=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].firstName").value("John"))
				.andExpect(jsonPath("$[0].lastName").value("Boyd"))
				.andExpect(jsonPath("$[0].address").value("1509 Culver St"))
				.andExpect(jsonPath("$[0].phone").value("841-874-6512")).andExpect(jsonPath("$[1]").exists())
				.andExpect(jsonPath("$[1].adultCount").exists()).andExpect(jsonPath("$[1].childCount").exists())
				.andExpect(jsonPath("$[1].adultCount").value(1)).andExpect(jsonPath("$[1].childCount").value(0));
	}

	@Test
	public void testGetPhoneNumbersByFirestation() throws Exception {
		List<String> mockPhoneNumbers = Arrays.asList("123-456-7890", "987-654-3210");

		when(firestationService.getPhoneNumbersByFirestation(any(Integer.class))).thenReturn(mockPhoneNumbers);

		mockMvc.perform(get("/phoneAlert?firestation=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0]").value("123-456-7890"))
				.andExpect(jsonPath("$[1]").value("987-654-3210"));
	}
	
	@Test
	public void testGetResidentsByStations() throws Exception {
		List<Map<String, Object>> persons = new ArrayList<>();
		
		Map<String, Object> person = new HashMap<>();
		person.put("firstName", "John");
		person.put("lastName", "Boyd");
		person.put("age", 36);
		person.put("medications", Arrays.asList("aznol:350mg", "hydrapermazol:100mg"));
		person.put("allergies", Arrays.asList("nillacilan"));
		person.put("phone", "841-874-6512");
		
		persons.add(person);
		
	    when(firestationService.getPersonsByStations(Arrays.asList(1, 2)))
	        .thenReturn(persons);

	    mockMvc.perform(get("/flood?stations=1,2").contentType(MediaType.APPLICATION_JSON))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$[0].firstName").value("John"))
	        .andExpect(jsonPath("$[0].lastName").value("Boyd"))
	        .andExpect(jsonPath("$[0].age").value("36"))
	        .andExpect(jsonPath("$[0].medications[0]").value("aznol:350mg"))
	        .andExpect(jsonPath("$[0].medications[1]").value("hydrapermazol:100mg"))
	        .andExpect(jsonPath("$[0].allergies[0]").value("nillacilan"));
	}
}
