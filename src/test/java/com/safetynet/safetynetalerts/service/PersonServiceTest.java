package com.safetynet.safetynetalerts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.FirestationRepository;
import com.safetynet.safetynetalerts.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

	@Mock
	private static PersonRepository personRepository;

	@Mock
	private static MedicalRecordService medicalRecordService;

	@Mock
	private static FirestationRepository firestationRepository;

	@InjectMocks
	private PersonService personService;

	private static Person TEST_PERSON;

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
	}

	@Test
	public void testPostPerson() {
		when(personRepository.findByFirstNameAndLastName(eq("John"), eq("Boyd"))).thenReturn(Optional.empty());
		when(personRepository.save(any(Person.class))).thenReturn(TEST_PERSON);

		Person result = personService.postPerson(TEST_PERSON);

		verify(personRepository, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(personRepository, times(1)).save(TEST_PERSON);

		assertNotNull(result);
		assertEquals("1509 Culver St", result.getAddress());
		assertEquals("John", result.getFirstName());
		assertEquals("Boyd", result.getLastName());
		assertEquals("Culver", result.getCity());
		assertEquals("jaboyd@email.com", result.getEmail());
		assertEquals("841-874-6512", result.getPhone());
		assertEquals("97451", result.getZip());
	}

	@Test
	void testPostPersonConflict() {
		when(personRepository.findByFirstNameAndLastName(eq("John"), eq("Boyd"))).thenReturn(Optional.of(TEST_PERSON));

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> personService.postPerson(TEST_PERSON));

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This person already exists"));

		verify(personRepository, times(1)).findByFirstNameAndLastName(eq("John"), eq("Boyd"));
		verify(personRepository, never()).save(any());
	}

	@Test
	public void testPutPerson() {
		Person newPerson = new Person();
		newPerson.setAddress("3457 Main St");
		newPerson.setCity("Springfield");
		newPerson.setEmail("new.email@example.com");
		newPerson.setPhone("123-456-7890");
		newPerson.setZip("12345");

		when(personRepository.findByFirstNameAndLastName(eq("John"), eq("Boyd"))).thenReturn(Optional.of(TEST_PERSON));
		when(personRepository.save(any(Person.class))).thenReturn(TEST_PERSON);

		Person result = personService.putPerson("John", "Boyd", newPerson);

		verify(personRepository, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(personRepository, times(1)).save(TEST_PERSON);

		assertNotNull(result);
		assertEquals("3457 Main St", result.getAddress());
		assertEquals("Springfield", result.getCity());
		assertEquals("new.email@example.com", result.getEmail());
		assertEquals("123-456-7890", result.getPhone());
		assertEquals("12345", result.getZip());
	}

	@Test
	void testPutPersonNotFound() {
		when(personRepository.findByFirstNameAndLastName(eq("John"), eq("Boyd"))).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> personService.putPerson("John", "Boyd", TEST_PERSON));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This person doesn't exist"));

		verify(personRepository, times(1)).findByFirstNameAndLastName(eq("John"), eq("Boyd"));
		verify(personRepository, never()).save(any());
	}

	@Test
	public void testDeletePerson() {
		when(personRepository.findByFirstNameAndLastName(eq("John"), eq("Boyd"))).thenReturn(Optional.of(TEST_PERSON));

		String result = personService.deletePerson("John", "Boyd");

		verify(personRepository, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(personRepository, times(1)).delete(TEST_PERSON);

		assertNotNull(result);
		assertEquals("Person deleted", result);
	}

	@Test
	void testDeletePersonNotFound() {
		when(personRepository.findByFirstNameAndLastName(eq("John"), eq("Boyd"))).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> personService.deletePerson("John", "Boyd"));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This person doesn't exist"));

		verify(personRepository, times(1)).findByFirstNameAndLastName(eq("John"), eq("Boyd"));
		verify(personRepository, never()).delete(any());
	}

	@Test
	public void getChildsByAddress() {
		Person child = new Person();
		child.setFirstName("John");
		child.setLastName("Doe");
		child.setAddress("1509 Culver St");
		child.setCity("Culver");
		child.setZip("97451");
		child.setPhone("111-222-3333");
		child.setEmail("test@test.com");

		MedicalRecord childMedicalRecord = new MedicalRecord();
		childMedicalRecord.setBirthdate("01/15/2010");
		child.setMedicalRecord(childMedicalRecord);

		MedicalRecord adultMedicalRecord = new MedicalRecord();
		adultMedicalRecord.setBirthdate("01/15/1980");
		TEST_PERSON.setMedicalRecord(adultMedicalRecord);

		when(personRepository.findByAddress(any())).thenReturn(Arrays.asList(child, TEST_PERSON));
		when(medicalRecordService.calculateAge(anyString())).thenReturn(13).thenReturn(43);

		List<Map<String, Object>> result = personService.getChildsByAddress("1509 Culver St");

		verify(personRepository, times(1)).findByAddress(any());
		verify(medicalRecordService, times(3)).calculateAge(any());

		assertNotNull(result);

		Map<String, Object> childMap = result.get(0);
		assertEquals("John", childMap.get("firstName"));
		assertEquals("Doe", childMap.get("lastName"));
		assertEquals(13, childMap.get("age"));

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> otherMembers = (List<Map<String, Object>>) childMap.get("otherMembers");
		assertNotNull(otherMembers);
		assertEquals("John", otherMembers.get(0).get("firstName"));
		assertEquals("Boyd", otherMembers.get(0).get("lastName"));
		assertEquals(43, otherMembers.get(0).get("age"));
	}

	@Test
	public void getPersonsAndFirestationByAddress() {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Doe");
		person.setPhone("111-222-3333");
		person.setAddress("123 Main St");

		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setBirthdate("01/15/1980");
		medicalRecord.setMedications(Arrays.asList("Med1", "Med2"));
		medicalRecord.setAllergies(Arrays.asList("Allergy1", "Allergy2"));
		person.setMedicalRecord(medicalRecord);

		List<Person> persons = Arrays.asList(person);

		Firestation firestation = new Firestation();
		firestation.setStation(1);
		firestation.setAddress("123 Main St");

		when(personRepository.findByAddress(any())).thenReturn(persons);
		when(firestationRepository.findByAddress(any())).thenReturn(Optional.of(firestation));
		when(medicalRecordService.calculateAge("01/15/1980")).thenReturn(42);

		List<Map<String, Object>> result = personService.getPersonsAndFirestationByAddress("123 Main St");

		verify(personRepository, times(1)).findByAddress("123 Main St");
		verify(firestationRepository, times(1)).findByAddress("123 Main St");
		verify(medicalRecordService, times(1)).calculateAge("01/15/1980");

		assertNotNull(result);
		assertEquals(2, result.size());

		Map<String, Object> residentMap = result.get(0);
		assertEquals("John", residentMap.get("firstName"));
		assertEquals("Doe", residentMap.get("lastName"));
		assertEquals("111-222-3333", residentMap.get("phone"));
		assertEquals(42, residentMap.get("age"));

		@SuppressWarnings("unchecked")
		List<String> medications = (List<String>) residentMap.get("medications");
		@SuppressWarnings("unchecked")
		List<String> allergies = (List<String>) residentMap.get("allergies");

		assertNotNull(medications);
		assertNotNull(allergies);
		assertEquals(2, medications.size());
		assertEquals(2, allergies.size());

		Map<String, Object> firestationInfo = result.get(1);
		assertEquals(1, firestationInfo.get("firestationNumber"));
	}

	@Test
	public void getPersonsAndFirestationByAddressNoPerson() {
		when(personRepository.findByAddress(any())).thenReturn(Arrays.asList());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> personService.getPersonsAndFirestationByAddress("123 Main St"));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("No residents found for this address"));

		verify(personRepository, times(1)).findByAddress("123 Main St");
		verify(firestationRepository, never()).findByAddress(any());
		verify(medicalRecordService, never()).calculateAge(any());
	}

	@Test
	public void getPersonsAndFirestationByAddressNoFirestation() {
		when(personRepository.findByAddress(any())).thenReturn(Arrays.asList(TEST_PERSON));
		when(firestationRepository.findByAddress(any())).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> personService.getPersonsAndFirestationByAddress("123 Main St"));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("No firestation found for this address"));

		verify(personRepository, times(1)).findByAddress("123 Main St");
		verify(firestationRepository, times(1)).findByAddress("123 Main St");
		verify(medicalRecordService, never()).calculateAge(any());
	}

	@Test
	public void getPersonInfoByFirstNameAndLastName() {
		String firstName = "John";
		String lastName = "Doe";

		Person person = new Person();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setAddress("123 Main St");
		person.setEmail("john.doe@example.com");

		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setBirthdate("01/15/1980");
		medicalRecord.setMedications(Arrays.asList("Med1", "Med2"));
		medicalRecord.setAllergies(Arrays.asList("Allergy1", "Allergy2"));
		person.setMedicalRecord(medicalRecord);

		when(personRepository.findByFirstNameAndLastName(firstName, lastName)).thenReturn(Optional.of(person));
		when(medicalRecordService.calculateAge("01/15/1980")).thenReturn(42);

		List<Map<String, Object>> result = personService.getPersonInfoByFirstNameAndLastName(firstName, lastName);

		assertNotNull(result);
		assertEquals(1, result.size());

		Map<String, Object> personMap = result.get(0);
		assertEquals(firstName, personMap.get("firstName"));
		assertEquals(lastName, personMap.get("lastName"));
		assertEquals("123 Main St", personMap.get("address"));
		assertEquals("john.doe@example.com", personMap.get("email"));
		assertEquals(42, personMap.get("age"));

		@SuppressWarnings("unchecked")
		List<String> medications = (List<String>) personMap.get("medications");
		@SuppressWarnings("unchecked")
		List<String> allergies = (List<String>) personMap.get("allergies");

		assertNotNull(medications);
		assertNotNull(allergies);
		assertEquals(2, medications.size());
		assertEquals(2, allergies.size());
		assertEquals("Med1", medications.get(0));
		assertEquals("Allergy2", allergies.get(1));
	}

	@Test
	public void getPersonInfoByFirstNameAndLastNameNotFound() {
		String firstName = "John";
		String lastName = "Doe";

		when(personRepository.findByFirstNameAndLastName(firstName, lastName)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> personService.getPersonInfoByFirstNameAndLastName(firstName, lastName));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("No person found with the given name"));

		verify(personRepository, times(1)).findByFirstNameAndLastName(firstName, lastName);
		verify(medicalRecordService, never()).calculateAge(any());
	}

	@Test
	public void getCommunityEmails() {
		String city = "Culver";

		Person person1 = new Person();
		person1.setEmail("john.doe@example.com");

		Person person2 = new Person();
		person2.setEmail("jane.smith@example.com");

		List<Person> residents = Arrays.asList(person1, person2);

		when(personRepository.findByCity(city)).thenReturn(residents);

		List<String> result = personService.getCommunityEmails(city);

		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.contains("john.doe@example.com"));
		assertTrue(result.contains("jane.smith@example.com"));
	}

	@Test
	public void getCommunityEmailsNoPerson() {
		String city = "Culver";

		when(personRepository.findByCity(city)).thenReturn(Arrays.asList());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> personService.getCommunityEmails(city));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("No residents found for the city"));

		verify(personRepository, times(1)).findByCity(city);
	}
}
