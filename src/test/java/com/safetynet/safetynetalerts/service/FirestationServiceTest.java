package com.safetynet.safetynetalerts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
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
public class FirestationServiceTest {

	@InjectMocks
	private FirestationService firestationService;

	@Mock
	private static FirestationRepository firestationRepository;

	@Mock
	private static PersonRepository personRepository;

	@Mock
	private static MedicalRecordService medicalRecordService;

	private static Firestation TEST_FIRESTATION;

	@BeforeEach
	public void setUpPerTest() {
		TEST_FIRESTATION = new Firestation();
		TEST_FIRESTATION.setAddress("1509 Culver St");
		TEST_FIRESTATION.setStation(3);
	}

	@Test
	public void testPostFirestation() {
		when(firestationRepository.findByAddress(eq("1509 Culver St"))).thenReturn(Optional.empty());
		when(firestationRepository.save(any(Firestation.class))).thenReturn(TEST_FIRESTATION);

		Firestation result = firestationService.postFirestation(TEST_FIRESTATION);

		verify(firestationRepository, times(1)).findByAddress("1509 Culver St");
		verify(firestationRepository, times(1)).save(TEST_FIRESTATION);

		assertNotNull(result);
		assertEquals("1509 Culver St", result.getAddress());
		assertEquals(3, result.getStation());
	}

	@Test
	public void testPostFirestationAlreadyExist() {
		Firestation existingFirestation = new Firestation();
		existingFirestation.setAddress("1509 Culver St");
		existingFirestation.setStation(3);

		Firestation newFirestation = new Firestation();
		newFirestation.setAddress("1509 Culver St");
		newFirestation.setStation(4);

		when(firestationRepository.findByAddress(anyString())).thenReturn(Optional.of(existingFirestation));

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> firestationService.postFirestation(newFirestation));

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
		assertTrue(exception.getReason().contains("Firestation is already assigned for this address"));
		assertTrue(exception.getReason().contains(newFirestation.getAddress()));

		verify(firestationRepository, times(1)).findByAddress(newFirestation.getAddress());
		verify(firestationRepository, never()).save(newFirestation);
	}

	@Test
	public void testPutFirestation() {
		Firestation newFirestation = new Firestation();
		newFirestation.setStation(4);

		when(firestationRepository.findByAddress(eq("1509 Culver St"))).thenReturn(Optional.of(TEST_FIRESTATION));
		when(firestationRepository.save(any(Firestation.class))).thenReturn(TEST_FIRESTATION);

		Firestation result = firestationService.putFirestation("1509 Culver St", newFirestation);

		verify(firestationRepository, times(1)).findByAddress("1509 Culver St");
		verify(firestationRepository, times(1)).save(TEST_FIRESTATION);

		assertNotNull(result);
		assertEquals(4, result.getStation());
	}

	@Test
	public void testPutFirestationNotFound() {
		when(firestationRepository.findByAddress(anyString())).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> firestationService.putFirestation(TEST_FIRESTATION.getAddress(), TEST_FIRESTATION));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This firestation doesn't exist"));

		verify(firestationRepository, times(1)).findByAddress(TEST_FIRESTATION.getAddress());
		verify(firestationRepository, never()).save(TEST_FIRESTATION);
	}

	@Test
	public void testDeleteFirestation() {
		when(firestationRepository.findByAddress(eq("1509 Culver St"))).thenReturn(Optional.of(TEST_FIRESTATION));

		String result = firestationService.deleteFirestation("1509 Culver St");

		verify(firestationRepository, times(1)).findByAddress("1509 Culver St");
		verify(firestationRepository, times(1)).delete(TEST_FIRESTATION);

		assertNotNull(result);
		assertEquals("Firestation object deleted", result);
	}

	@Test
	public void testDeleteFirestationNotFound() {
		when(firestationRepository.findByAddress(anyString())).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> firestationService.deleteFirestation(TEST_FIRESTATION.getAddress()));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This firestation doesn't exist"));

		verify(firestationRepository, times(1)).findByAddress(TEST_FIRESTATION.getAddress());
		verify(firestationRepository, never()).delete(TEST_FIRESTATION);
	}

	@Test
	public void testGetListOfPersonByFirestation() {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Boyd");
		person.setAddress("1509 Culver St");
		person.setCity("Culver");
		person.setZip("97451");
		person.setPhone("841-874-6512");
		person.setEmail("jaboyd@email.com");

		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setBirthdate("03/06/1984");
		medicalRecord.setPerson(person);

		person.setMedicalRecord(medicalRecord);

		List<Firestation> firestations = Arrays.asList(TEST_FIRESTATION);
		List<Person> residents = Arrays.asList(person);

		when(firestationRepository.findAllByStation(3)).thenReturn(firestations);
		when(personRepository.findByAddress(eq("1509 Culver St"))).thenReturn(residents);
		when(medicalRecordService.calculateAge(person.getMedicalRecord().getBirthdate())).thenReturn(39);

		List<Map<String, Object>> result = firestationService.getListOfPersonByFirestation(3);

		verify(firestationRepository, times(1)).findAllByStation(3);
		verify(personRepository, times(1)).findByAddress("1509 Culver St");
		verify(medicalRecordService, times(1)).calculateAge("03/06/1984");

		assertNotNull(result);

		assertEquals(2, result.size());

		Map<String, Object> personMap = result.get(0);
		assertEquals("John", personMap.get("firstName"));
		assertEquals("Boyd", personMap.get("lastName"));
		assertEquals("1509 Culver St", personMap.get("address"));
		assertEquals("841-874-6512", personMap.get("phone"));

		Map<String, Object> countResult = result.get(1);
		assertEquals(1, countResult.get("adultCount"));
		assertEquals(0, countResult.get("childCount"));
	}

	@Test
	public void testGetListOfPersonByFirestationWhenChild() {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Boyd");
		person.setAddress("1509 Culver St");
		person.setCity("Culver");
		person.setZip("97451");
		person.setPhone("841-874-6512");
		person.setEmail("jaboyd@email.com");

		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setBirthdate("03/06/2010");
		medicalRecord.setPerson(person);

		person.setMedicalRecord(medicalRecord);

		List<Firestation> firestations = Arrays.asList(TEST_FIRESTATION);
		List<Person> residents = Arrays.asList(person);

		when(firestationRepository.findAllByStation(3)).thenReturn(firestations);
		when(personRepository.findByAddress(eq("1509 Culver St"))).thenReturn(residents);
		when(medicalRecordService.calculateAge(person.getMedicalRecord().getBirthdate())).thenReturn(13);

		List<Map<String, Object>> result = firestationService.getListOfPersonByFirestation(3);

		verify(firestationRepository, times(1)).findAllByStation(3);
		verify(personRepository, times(1)).findByAddress("1509 Culver St");
		verify(medicalRecordService, times(1)).calculateAge("03/06/2010");

		assertNotNull(result);

		assertEquals(2, result.size());

		Map<String, Object> personMap = result.get(0);
		assertEquals("John", personMap.get("firstName"));
		assertEquals("Boyd", personMap.get("lastName"));
		assertEquals("1509 Culver St", personMap.get("address"));
		assertEquals("841-874-6512", personMap.get("phone"));

		Map<String, Object> countResult = result.get(1);
		assertEquals(0, countResult.get("adultCount"));
		assertEquals(1, countResult.get("childCount"));
	}

	@Test
	public void testGetListOfPersonByFirestationNotFound() {
		when(firestationRepository.findAllByStation(anyInt())).thenReturn(Collections.emptyList());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> firestationService.getListOfPersonByFirestation(3));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This firestation doesn't exist"));

		verify(firestationRepository, times(1)).findAllByStation(3);
		verify(personRepository, never()).findByAddress(anyString());
		verify(medicalRecordService, never()).calculateAge(anyString());
	}

	@Test
	public void testGetPhoneNumbersByFirestation() {
		Firestation firestation = new Firestation();
		firestation.setStation(1);
		firestation.setAddress("123 Main St");

		Person person1 = new Person();
		person1.setPhone("111-222-3333");

		Person person2 = new Person();
		person2.setPhone("444-555-6666");

		List<Firestation> firestations = Arrays.asList(firestation);
		List<Person> residents = Arrays.asList(person1, person2);

		when(firestationRepository.findAllByStation(anyInt())).thenReturn(firestations);
		when(personRepository.findByAddress("123 Main St")).thenReturn(residents);

		List<String> result = firestationService.getPhoneNumbersByFirestation(1);

		verify(firestationRepository, times(1)).findAllByStation(1);
		verify(personRepository, times(1)).findByAddress("123 Main St");

		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.contains("111-222-3333"));
		assertTrue(result.contains("444-555-6666"));
	}

	@Test
	public void testGetPhoneNumbersByFirestationNotFound() {
		when(firestationRepository.findAllByStation(TEST_FIRESTATION.getStation())).thenReturn(Arrays.asList());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> firestationService.getPhoneNumbersByFirestation(TEST_FIRESTATION.getStation()));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("No firestation found for this number"));

		verify(firestationRepository, times(1)).findAllByStation(TEST_FIRESTATION.getStation());
		verify(personRepository, never()).findByAddress(anyString());
	}

	@Test
	public void testGetPersonsByStations() {
		Firestation firestation1 = new Firestation();
		firestation1.setStation(1);
		firestation1.setAddress("123 Main St");

		Firestation firestation2 = new Firestation();
		firestation2.setStation(2);
		firestation2.setAddress("456 Oak St");

		List<Firestation> firestations1 = Arrays.asList(firestation1);
		List<Firestation> firestations2 = Arrays.asList(firestation2);

		Person person1 = new Person();
		person1.setAddress("123 Main St");
		person1.setFirstName("John");
		person1.setLastName("Doe");
		person1.setPhone("111-222-3333");

		MedicalRecord medicalRecord1 = new MedicalRecord();
		medicalRecord1.setBirthdate("01/15/1980");
		medicalRecord1.setMedications(Arrays.asList("Med1", "Med2"));
		medicalRecord1.setAllergies(Arrays.asList("Allergy1", "Allergy2"));
		person1.setMedicalRecord(medicalRecord1);

		Person person2 = new Person();
		person2.setAddress("456 Oak St");
		person2.setFirstName("Jane");
		person2.setLastName("Smith");
		person2.setPhone("444-555-6666");

		MedicalRecord medicalRecord2 = new MedicalRecord();
		medicalRecord2.setBirthdate("05/20/1995");
		medicalRecord2.setMedications(Arrays.asList("Med3", "Med4"));
		medicalRecord2.setAllergies(Arrays.asList("Allergy3", "Allergy4"));
		person2.setMedicalRecord(medicalRecord2);

		List<Person> persons1 = Arrays.asList(person1);
		List<Person> persons2 = Arrays.asList(person2);

		when(firestationRepository.findAllByStation(1)).thenReturn(firestations1);
		when(firestationRepository.findAllByStation(2)).thenReturn(firestations2);
		when(personRepository.findByAddress("123 Main St")).thenReturn(persons1);
		when(personRepository.findByAddress("456 Oak St")).thenReturn(persons2);
		when(medicalRecordService.calculateAge("01/15/1980")).thenReturn(42);
		when(medicalRecordService.calculateAge("05/20/1995")).thenReturn(27);

		List<Map<String, Object>> result = firestationService.getPersonsByStations(Arrays.asList(1, 2));

		verify(firestationRepository, times(2)).findAllByStation(anyInt());
		verify(personRepository, times(1)).findByAddress("123 Main St");
		verify(personRepository, times(1)).findByAddress("456 Oak St");
		verify(medicalRecordService, times(1)).calculateAge("01/15/1980");
		verify(medicalRecordService, times(1)).calculateAge("05/20/1995");

		assertNotNull(result);
		assertEquals(2, result.size());

		Map<String, Object> personMap1 = result.get(0);
		assertEquals("123 Main St", personMap1.get("address"));
		assertEquals("John", personMap1.get("firstName"));
		assertEquals("Doe", personMap1.get("lastName"));
		assertEquals("111-222-3333", personMap1.get("phone"));
		assertEquals(42, personMap1.get("age"));

		@SuppressWarnings("unchecked")
		List<String> medications1 = (List<String>) personMap1.get("medications");
		@SuppressWarnings("unchecked")
		List<String> allergies1 = (List<String>) personMap1.get("allergies");

		assertNotNull(medications1);
		assertNotNull(allergies1);
		assertEquals(2, medications1.size());
		assertEquals(2, allergies1.size());

		assertEquals(1, personMap1.get("firestationNumber"));

		Map<String, Object> personMap2 = result.get(1);
		assertEquals("456 Oak St", personMap2.get("address"));
		assertEquals("Jane", personMap2.get("firstName"));
		assertEquals("Smith", personMap2.get("lastName"));
		assertEquals("444-555-6666", personMap2.get("phone"));
		assertEquals(27, personMap2.get("age"));

		@SuppressWarnings("unchecked")
		List<String> medications2 = (List<String>) personMap2.get("medications");
		@SuppressWarnings("unchecked")
		List<String> allergies2 = (List<String>) personMap2.get("allergies");

		assertNotNull(medications2);
		assertNotNull(allergies2);
		assertEquals(2, medications2.size());
		assertEquals(2, allergies2.size());

		assertEquals(2, personMap2.get("firestationNumber"));
	}

}
