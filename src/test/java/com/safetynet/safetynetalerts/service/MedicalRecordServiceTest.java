package com.safetynet.safetynetalerts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.safetynet.safetynetalerts.CRUD.MedicalRecordCRUD;
import com.safetynet.safetynetalerts.CRUD.PersonCRUD;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {

	@InjectMocks
	private MedicalRecordService medicalRecordService;

	@Mock
	private static MedicalRecordCRUD medicalRecordCRUD;

	@Mock
	private static PersonCRUD personCRUD;

	@Test
	public void testPostMedicalRecord() throws IOException {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Boyd");

		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setFirstName("John");
		medicalRecord.setLastName("Boyd");
		medicalRecord.setBirthdate("03/06/1984");
		medicalRecord.setAllergies(Arrays.asList("nillacilan"));
		medicalRecord.setMedications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg"));

		when(personCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(person);
		when(medicalRecordCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(null);
		when(medicalRecordCRUD.save(medicalRecord)).thenReturn(medicalRecord);

		MedicalRecord result = medicalRecordService.postMedicalRecord(medicalRecord);

		verify(personCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(medicalRecordCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(medicalRecordCRUD, times(1)).save(medicalRecord);

		assertNotNull(result);

		assertEquals("John", result.getFirstName());
		assertEquals("Boyd", result.getLastName());
		assertEquals("03/06/1984", result.getBirthdate());
		assertEquals("nillacilan", result.getAllergies().get(0));
		assertEquals("aznol:350mg", result.getMedications().get(0));
		assertEquals("hydrapermazol:100mg", result.getMedications().get(1));
	}

	@Test
	public void testPostMedicalRecordPersonNotFound() throws IOException {
		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setFirstName("John");
		medicalRecord.setLastName("Boyd");
		medicalRecord.setBirthdate("03/06/1984");

		when(personCRUD.findByFirstNameAndLastName(medicalRecord.getFirstName(), medicalRecord.getLastName()))
				.thenReturn(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> medicalRecordService.postMedicalRecord(medicalRecord));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This person doesn't exist"));

		verify(personCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(medicalRecordCRUD, never()).findByFirstNameAndLastName(any(), any());
		verify(medicalRecordCRUD, never()).save(any());
	}

	@Test
	public void testPostMedicalRecordConflict() throws IOException {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Boyd");

		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setFirstName("John");
		medicalRecord.setLastName("Boyd");

		when(personCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(person);
		when(medicalRecordCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(medicalRecord);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> medicalRecordService.postMedicalRecord(medicalRecord));

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This Medical record already exists"));

		verify(personCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(medicalRecordCRUD, times(1)).findByFirstNameAndLastName(any(), any());
		verify(medicalRecordCRUD, never()).save(any());
	}

	@Test
	public void testPutMedicalRecord() throws IOException {
		MedicalRecord oldMedicalRecord = new MedicalRecord();
		oldMedicalRecord.setFirstName("John");
		oldMedicalRecord.setLastName("Boyd");
		oldMedicalRecord.setBirthdate("03/06/1984");

		MedicalRecord newMedicalRecord = new MedicalRecord();
		newMedicalRecord.setFirstName("John");
		newMedicalRecord.setLastName("Boyd");
		newMedicalRecord.setBirthdate("12/02/2003");

		when(medicalRecordCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(oldMedicalRecord);
		when(medicalRecordCRUD.save(oldMedicalRecord)).thenReturn(oldMedicalRecord);

		MedicalRecord result = medicalRecordService.putMedicalRecord("John", "Boyd", newMedicalRecord);

		verify(medicalRecordCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(medicalRecordCRUD, times(1)).save(oldMedicalRecord);

		assertNotNull(result);
		assertEquals("12/02/2003", result.getBirthdate());
	}

	@Test
	public void testPutMedicalRecordMedicalRecordNotFound() throws IOException {
		MedicalRecord newMedicalRecord = new MedicalRecord();
		newMedicalRecord.setFirstName("John");
		newMedicalRecord.setLastName("Boyd");
		newMedicalRecord.setBirthdate("12/02/2003");

		when(medicalRecordCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> medicalRecordService.putMedicalRecord("John", "Boyd", newMedicalRecord));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This medical record doesn't exist"));

		verify(medicalRecordCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(medicalRecordCRUD, never()).save(any());
	}

	@Test
	public void testDeleteMedicalRecord() throws IOException {
		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setFirstName("John");
		medicalRecord.setLastName("Boyd");
		medicalRecord.setBirthdate("12/02/2003");

		when(medicalRecordCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(medicalRecord);

		String result = medicalRecordService.deleteMedicalRecord("John", "Boyd");

		verify(medicalRecordCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(medicalRecordCRUD, times(1)).delete(medicalRecord);

		assertNotNull(result);
		assertEquals("Medical record deleted", result);
	}

	@Test
	public void testDeleteMedicalRecordMedicalRecordNotFound() throws IOException {

		when(medicalRecordCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> medicalRecordService.deleteMedicalRecord("John", "Boyd"));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This medical record doesn't exist"));

		verify(medicalRecordCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(medicalRecordCRUD, never()).delete(any());
	}

	@Test
	public void testCalculateAge() {
		String birthdate = "03/06/1984";
		int result = medicalRecordService.calculateAge(birthdate);

		assertEquals(39, result);
	}
}
