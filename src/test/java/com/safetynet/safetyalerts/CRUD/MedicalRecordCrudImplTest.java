package com.safetynet.safetyalerts.CRUD;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.CRUD.MedicalRecordCrudImpl;
import com.safetynet.safetynetalerts.configuration.DataStore;
import com.safetynet.safetynetalerts.model.MedicalRecord;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordCrudImplTest {

	@Mock
	private DataStore dataStore;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private MedicalRecordCrudImpl medicalRecordCrudImpl;

	@Test
	public void testSaveMedicalRecord() throws IOException {
		MedicalRecord medicalRecordToSave = new MedicalRecord();
		medicalRecordToSave.setFirstName("John");
		medicalRecordToSave.setLastName("Boyd");
		medicalRecordToSave.setBirthdate("03/06/1984");

		List<MedicalRecord> currentMedicalRecords = new ArrayList<>();
		when(dataStore.getMedicalrecords()).thenReturn(currentMedicalRecords);

		Mockito.doNothing().when(dataStore).saveToFile(objectMapper);

		MedicalRecord savedMedicalRecord = medicalRecordCrudImpl.save(medicalRecordToSave);

		assertEquals(1, currentMedicalRecords.size());
		assertEquals(medicalRecordToSave, currentMedicalRecords.get(0));

		verify(dataStore, times(1)).saveToFile(objectMapper);

		assertEquals(medicalRecordToSave, savedMedicalRecord);
	}

	@Test
	public void testFindByFirstNameAndLastName() throws IOException {

		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setFirstName("John");
		medicalRecord.setLastName("Doe");

		List<MedicalRecord> medicalRecords = new ArrayList<>();
		medicalRecords.add(medicalRecord);

		when(dataStore.getMedicalrecords()).thenReturn(medicalRecords);

		MedicalRecord result = medicalRecordCrudImpl.findByFirstNameAndLastName("John", "Doe");

		assertNotNull(result);
		assertEquals("John", result.getFirstName());
		assertEquals("Doe", result.getLastName());

		verify(dataStore, times(1)).getMedicalrecords();
	}

	@Test
	public void testDeleteMedicalRecord() throws IOException {
		MedicalRecord medicalRecordToDelete = new MedicalRecord();
		medicalRecordToDelete.setFirstName("John");
		medicalRecordToDelete.setLastName("Doe");

		List<MedicalRecord> medicalRecords = new ArrayList<>();
		medicalRecords.add(medicalRecordToDelete);

		when(dataStore.getMedicalrecords()).thenReturn(medicalRecords);

		Mockito.doNothing().when(dataStore).saveToFile(objectMapper);

		medicalRecordCrudImpl.delete(medicalRecordToDelete);

		assertTrue(medicalRecords.isEmpty());

		verify(dataStore, times(1)).saveToFile(objectMapper);
	}
}
