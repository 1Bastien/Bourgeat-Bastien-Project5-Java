package com.safetynet.safetynetalerts.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.service.MedicalRecordService;

@WebMvcTest(MedicalRecordController.class)
public class MedicalRecordControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MedicalRecordService medicalRecordService;

	@Test
	public void testPostMedicalRecord() throws Exception {
		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setBirthdate("03/06/1984");
		medicalRecord.setAllergies(Arrays.asList("nillacilan"));
		medicalRecord.setMedications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg"));

		when(medicalRecordService.postMedicalRecord("John", "Boyd", medicalRecord)).thenReturn(medicalRecord);

		mockMvc.perform(post("/medicalRecord/John/Boyd").contentType(MediaType.APPLICATION_JSON).content(
				"{\"birthdate\":\"03/06/1984\",\"medications\":[\"aznol:350mg\",\"hydrapermazol:100mg\"],\"allergies\":[\"nillacilan\"]}"))
				.andExpect(status().isOk()).andReturn();
	}

	@Test
	public void testPutMedicalRecord() throws Exception {
		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setBirthdate("03/06/1984");
		medicalRecord.setAllergies(Arrays.asList("nillacilan"));
		medicalRecord.setMedications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg"));

		when(medicalRecordService.putMedicalRecord("John", "Boyd", medicalRecord)).thenReturn(medicalRecord);

		mockMvc.perform(put("/medicalRecord/John/Boyd").contentType(MediaType.APPLICATION_JSON).content(
				"{\"birthdate\":\"03/06/1984\",\"medications\":[\"aznol:350mg\",\"hydrapermazol:100mg\"],\"allergies\":[\"nillacilan\"]}"))
				.andExpect(status().isOk()).andReturn();
	}

	@Test
	public void testDeleteMedicalRecord() throws Exception {
        when(medicalRecordService.deleteMedicalRecord("John", "Boyd")).thenReturn("Medical record deleted");

        mockMvc.perform(delete("/medicalRecord/John/Boyd")).andExpect(status().isOk()).andExpect(content().string("Medical record deleted")).andReturn();
    }
}
