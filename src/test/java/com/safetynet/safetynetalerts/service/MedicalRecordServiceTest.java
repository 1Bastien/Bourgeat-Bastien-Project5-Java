package com.safetynet.safetynetalerts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.MedicalRecordRepository;
import com.safetynet.safetynetalerts.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {

	@InjectMocks
	private MedicalRecordService medicalRecordService;

	@Mock
	private static MedicalRecordRepository medicalRecordRepository;

	@Mock
	private static PersonRepository personRepository;

	private static Person TEST_PERSON;
	private static MedicalRecord TEST_MEDICAL_RECORD;

	@BeforeEach
	public void setUpPerTest() {
		TEST_PERSON = new Person();
		TEST_PERSON.setFirstName("John");
		TEST_PERSON.setLastName("Boyd");
		TEST_PERSON.setAddress("1509 Culver St");
		TEST_PERSON.setCity("Culver");
		TEST_PERSON.setZip("97451");
		TEST_PERSON.setPhone("841-874-6512");
		TEST_PERSON.setEmail("jaboyd@email.com");

		TEST_MEDICAL_RECORD = new MedicalRecord();
		TEST_MEDICAL_RECORD.setBirthdate("03/06/1984");
		TEST_MEDICAL_RECORD.setAllergies(Arrays.asList("nillacilan"));
		TEST_MEDICAL_RECORD.setMedications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg"));
	}

	@Test
	public void testPostMedicalRecord() {
		when(personRepository.findByFirstNameAndLastName(eq("John"), eq("Boyd"))).thenReturn(Optional.of(TEST_PERSON));
		when(medicalRecordRepository.findByPerson(eq(TEST_PERSON))).thenReturn(Optional.empty());
		when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(TEST_MEDICAL_RECORD);

		MedicalRecord result = medicalRecordService.postMedicalRecord("John", "Boyd", TEST_MEDICAL_RECORD);

		verify(personRepository, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(medicalRecordRepository, times(1)).findByPerson(TEST_PERSON);
		verify(medicalRecordRepository, times(1)).save(TEST_MEDICAL_RECORD);

		assertNotNull(result);

		assertEquals("03/06/1984", result.getBirthdate());
		assertEquals("nillacilan", result.getAllergies().get(0));
		assertEquals("aznol:350mg", result.getMedications().get(0));
		assertEquals("hydrapermazol:100mg", result.getMedications().get(1));
		assertEquals(TEST_PERSON, result.getPerson());
	}

	@Test
	void testPostMedicalRecordPersonNotFound() {
		String firstName = "John";
		String lastName = "Doe";
		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setBirthdate("2000-01-01");
		medicalRecord.setAllergies(Arrays.asList("allergy1"));
		medicalRecord.setMedications(Arrays.asList("medication1"));

		when(personRepository.findByFirstNameAndLastName(eq(firstName), eq(lastName))).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> medicalRecordService.postMedicalRecord(firstName, lastName, TEST_MEDICAL_RECORD));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This Person doesn't exists"));

		verify(personRepository, times(1)).findByFirstNameAndLastName(firstName, lastName);
		verify(medicalRecordRepository, never()).findByPerson(any());
		verify(medicalRecordRepository, never()).save(any());
	}

	@Test
	void testPostMedicalRecordConflict() {
		String firstName = "John";
		String lastName = "Boyd";

		TEST_MEDICAL_RECORD.setPerson(TEST_PERSON);

		when(personRepository.findByFirstNameAndLastName(eq(firstName), eq(lastName)))
				.thenReturn(Optional.of(TEST_PERSON));
		when(medicalRecordRepository.findByPerson(any())).thenReturn(Optional.of(TEST_MEDICAL_RECORD));

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> medicalRecordService.postMedicalRecord(firstName, lastName, TEST_MEDICAL_RECORD));

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This Medical record already exists"));

		verify(personRepository, times(1)).findByFirstNameAndLastName(eq(firstName), eq(lastName));
		verify(medicalRecordRepository, times(1)).findByPerson(any());
		verify(medicalRecordRepository, never()).save(any());
	}

	@Test
	public void testPutMedicalRecord() {
		TEST_MEDICAL_RECORD.setPerson(TEST_PERSON);

		MedicalRecord newMedicalRecord = new MedicalRecord();
		newMedicalRecord.setBirthdate("12/02/2003");
		newMedicalRecord.setAllergies(Arrays.asList("peanut"));
		newMedicalRecord.setMedications(Arrays.asList("tradoxidine:400mg"));

		when(personRepository.findByFirstNameAndLastName(eq("John"), eq("Boyd"))).thenReturn(Optional.of(TEST_PERSON));
		when(medicalRecordRepository.findByPerson(eq(TEST_PERSON))).thenReturn(Optional.of(TEST_MEDICAL_RECORD));
		when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(TEST_MEDICAL_RECORD);

		MedicalRecord result = medicalRecordService.putMedicalRecord("John", "Boyd", newMedicalRecord);

		verify(personRepository, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(medicalRecordRepository, times(1)).findByPerson(TEST_PERSON);
		verify(medicalRecordRepository, times(1)).save(TEST_MEDICAL_RECORD);

		assertNotNull(result);
		assertEquals("12/02/2003", result.getBirthdate());
		assertEquals("peanut", result.getAllergies().get(0));
		assertEquals("tradoxidine:400mg", result.getMedications().get(0));
		assertEquals(TEST_PERSON, result.getPerson());
	}

	@Test
	public void testPutMedicalRecordPersonNotFound() {
		String firstName = "John";
		String lastName = "Doe";
		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setBirthdate("2000-01-01");
		medicalRecord.setAllergies(Arrays.asList("allergy1"));
		medicalRecord.setMedications(Arrays.asList("medication1"));

		when(personRepository.findByFirstNameAndLastName(eq(firstName), eq(lastName))).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> medicalRecordService.putMedicalRecord(firstName, lastName, TEST_MEDICAL_RECORD));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This person doesn't exist"));

		verify(personRepository, times(1)).findByFirstNameAndLastName(firstName, lastName);
		verify(medicalRecordRepository, never()).findByPerson(any());
		verify(medicalRecordRepository, never()).save(any());
	}

	@Test
	public void testPutMedicalRecordMedicalRecordNotFound() {
		String firstName = "John";
		String lastName = "Boyd";
		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setBirthdate("2000-01-01");
		medicalRecord.setAllergies(Arrays.asList("allergy1"));
		medicalRecord.setMedications(Arrays.asList("medication1"));

		when(personRepository.findByFirstNameAndLastName(eq(firstName), eq(lastName)))
				.thenReturn(Optional.of(TEST_PERSON));
		when(medicalRecordRepository.findByPerson(any())).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> medicalRecordService.putMedicalRecord(firstName, lastName, TEST_MEDICAL_RECORD));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This medical record doesn't exist"));

		verify(personRepository, times(1)).findByFirstNameAndLastName(firstName, lastName);
		verify(medicalRecordRepository, times(1)).findByPerson(TEST_PERSON);
		verify(medicalRecordRepository, never()).save(any());
	}

	@Test
	public void testDeleteMedicalRecord() {
		when(personRepository.findByFirstNameAndLastName(eq("John"), eq("Boyd"))).thenReturn(Optional.of(TEST_PERSON));
		when(medicalRecordRepository.findByPerson(eq(TEST_PERSON))).thenReturn(Optional.of(TEST_MEDICAL_RECORD));

		String result = medicalRecordService.deleteMedicalRecord("John", "Boyd");

		verify(personRepository, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(medicalRecordRepository, times(1)).findByPerson(TEST_PERSON);
		verify(medicalRecordRepository, times(1)).delete(TEST_MEDICAL_RECORD);

		assertNotNull(result);
		assertEquals("Medical record deleted", result);
	}
	
	@Test
	public void testDeleteMedicalRecordPersonNotFound() {
		when(personRepository.findByFirstNameAndLastName(eq("John"), eq("Boyd"))).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> medicalRecordService.deleteMedicalRecord("John", "Boyd"));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This person doesn't exist"));

		verify(personRepository, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(medicalRecordRepository, never()).findByPerson(any());
		verify(medicalRecordRepository, never()).delete(any());
	}
	
	@Test
	public void testDeleteMedicalRecordMedicalRecordNotFound() {
		String firstName = "John";
		String lastName = "Boyd";

		when(personRepository.findByFirstNameAndLastName(eq(firstName), eq(lastName)))
				.thenReturn(Optional.of(TEST_PERSON));
		when(medicalRecordRepository.findByPerson(eq(TEST_PERSON))).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> medicalRecordService.deleteMedicalRecord(firstName, lastName));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This medical record doesn't exist"));

		verify(personRepository, times(1)).findByFirstNameAndLastName(firstName, lastName);
		verify(medicalRecordRepository, times(1)).findByPerson(TEST_PERSON);
		verify(medicalRecordRepository, never()).delete(any());
	}

	@Test
	public void calculateAgeTest() {
		String birthdate = "03/06/1984";
		int result = medicalRecordService.calculateAge(birthdate);

		assertEquals(39, result);
	}
}
