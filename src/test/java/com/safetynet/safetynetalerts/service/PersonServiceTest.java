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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.safetynet.safetynetalerts.CRUD.FirestationCRUD;
import com.safetynet.safetynetalerts.CRUD.MedicalRecordCRUD;
import com.safetynet.safetynetalerts.CRUD.PersonCRUD;
import com.safetynet.safetynetalerts.DTO.CommunityEmailsDTO;
import com.safetynet.safetynetalerts.DTO.ListChildDTO;
import com.safetynet.safetynetalerts.DTO.PersonInfoDTO;
import com.safetynet.safetynetalerts.DTO.ResidentsAndFirestationDTO;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

	@Mock
	private static PersonCRUD personCRUD;

	@Mock
	private static MedicalRecordCRUD medicalRecordCRUD;

	@Mock
	private static MedicalRecordService medicalRecordService;

	@Mock
	private static FirestationCRUD firestationCRUD;

	@InjectMocks
	private PersonService personService;

	@Test
	public void testPostPerson() throws IOException {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Boyd");
		person.setAddress("1509 Culver St");
		person.setCity("Culver");
		person.setZip("97451");
		person.setPhone("841-874-6512");
		person.setEmail("test@test.com");

		when(personCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(null);
		when(personCRUD.save(person)).thenReturn(person);

		Person result = personService.postPerson(person);

		verify(personCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(personCRUD, times(1)).save(person);

		assertNotNull(result);
		assertEquals("1509 Culver St", result.getAddress());
		assertEquals("John", result.getFirstName());
		assertEquals("Boyd", result.getLastName());
		assertEquals("Culver", result.getCity());
		assertEquals("test@test.com", result.getEmail());
		assertEquals("841-874-6512", result.getPhone());
		assertEquals("97451", result.getZip());
	}

	@Test
	public void testPostPersonConflict() throws IOException {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Boyd");
		person.setAddress("1509 Culver St");
		person.setCity("Culver");
		person.setZip("97451");
		person.setPhone("841-874-6512");
		person.setEmail("test@test.com");

		when(personCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(person);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> personService.postPerson(person));

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This person already exists"));

		verify(personCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(personCRUD, never()).save(any());
	}

	@Test
	public void testPutPerson() throws IOException {
		Person oldPerson = new Person();
		oldPerson.setFirstName("John");
		oldPerson.setLastName("Boyd");
		oldPerson.setAddress("1509 Culver St");
		oldPerson.setPhone("841-874-6512");
		oldPerson.setCity("Culver");
		oldPerson.setZip("97451");
		oldPerson.setEmail("test@test.com");

		Person newPerson = new Person();
		newPerson.setFirstName("John");
		newPerson.setLastName("Boyd");
		newPerson.setAddress("3457 Main St");
		newPerson.setCity("Springfield");
		newPerson.setEmail("new.email@example.com");
		newPerson.setPhone("123-456-7890");
		newPerson.setZip("12345");

		when(personCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(oldPerson);
		when(personCRUD.save(oldPerson)).thenReturn(oldPerson);

		Person result = personService.putPerson("John", "Boyd", newPerson);

		verify(personCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(personCRUD, times(1)).save(oldPerson);

		assertNotNull(result);
		assertEquals("3457 Main St", result.getAddress());
		assertEquals("Springfield", result.getCity());
		assertEquals("new.email@example.com", result.getEmail());
		assertEquals("123-456-7890", result.getPhone());
		assertEquals("12345", result.getZip());
	}

	@Test
	public void testPutPersonNotFound() throws IOException {
		Person newPerson = new Person();
		newPerson.setFirstName("John");
		newPerson.setLastName("Boyd");
		newPerson.setAddress("3457 Main St");
		newPerson.setCity("Springfield");
		newPerson.setEmail("new.email@example.com");
		newPerson.setPhone("123-456-7890");
		newPerson.setZip("12345");

		when(personCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> personService.putPerson("John", "Boyd", newPerson));

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This person doesn't exist"));

		verify(personCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(personCRUD, never()).save(any());
	}

	@Test
	public void testDeletePerson() throws IOException {
		Person newPerson = new Person();
		newPerson.setFirstName("John");
		newPerson.setLastName("Boyd");
		newPerson.setAddress("3457 Main St");
		newPerson.setCity("Springfield");
		newPerson.setEmail("new.email@example.com");
		newPerson.setPhone("123-456-7890");
		newPerson.setZip("12345");

		when(personCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(newPerson);

		String result = personService.deletePerson("John", "Boyd");

		verify(personCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(personCRUD, times(1)).delete(newPerson);

		assertNotNull(result);
		assertEquals("Person deleted", result);
	}

	@Test
	public void testDeletePersonNotFound() throws IOException {
		when(personCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> personService.deletePerson("John", "Boyd"));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This person doesn't exist"));

		verify(personCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(personCRUD, never()).delete(any());
	}

	@Test
	public void testGetChildsByAddress() throws IOException {
		Person child = new Person();
		child.setFirstName("John");
		child.setLastName("Boyd");
		child.setAddress("1509 Culver St");
		child.setCity("Culver");
		child.setZip("97451");
		child.setPhone("111-222-3333");
		child.setEmail("test@test.com");

		Person parent = new Person();
		parent.setFirstName("Marc");
		parent.setLastName("Boyd");
		parent.setAddress("1509 Culver St");
		parent.setCity("Culver");
		parent.setZip("97451");
		parent.setPhone("111-222-3333");
		parent.setEmail("test@test.com");

		MedicalRecord parentMedicalRecord = new MedicalRecord();
		parentMedicalRecord.setFirstName("Marc");
		parentMedicalRecord.setLastName("Boyd");
		parentMedicalRecord.setBirthdate("01/15/1980");

		MedicalRecord childMedicalRecord = new MedicalRecord();
		childMedicalRecord.setFirstName("John");
		childMedicalRecord.setLastName("Boyd");
		childMedicalRecord.setBirthdate("01/15/2010");

		List<Person> residents = new ArrayList<>();
		residents.add(child);
		residents.add(parent);

		when(personCRUD.findByAddress("1509 Culver St")).thenReturn(residents);
		when(medicalRecordCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(childMedicalRecord);
		when(medicalRecordCRUD.findByFirstNameAndLastName("Marc", "Boyd")).thenReturn(parentMedicalRecord);
		when(medicalRecordService.calculateAge("01/15/2010")).thenReturn(13);
		when(medicalRecordService.calculateAge("01/15/1980")).thenReturn(42);

		ListChildDTO result = personService.getChildsByAddress("1509 Culver St");

		verify(personCRUD, times(1)).findByAddress("1509 Culver St");
		verify(medicalRecordService, times(1)).calculateAge("01/15/2010");

		assertNotNull(result);
		assertEquals(1, result.getChildren().size());

		ListChildDTO.ChildDTO childDTO = result.getChildren().get(0);
		assertEquals("John", childDTO.getFirstName());
		assertEquals("Boyd", childDTO.getLastName());
		assertEquals(13, childDTO.getAge());

		assertNotNull(childDTO.getOtherMembers());
		assertEquals(1, childDTO.getOtherMembers().size());
		ListChildDTO.OtherPersonDTO otherPersonDTO = childDTO.getOtherMembers().get(0);
		assertEquals("Marc", otherPersonDTO.getFirstName());
		assertEquals("Boyd", otherPersonDTO.getLastName());
		assertEquals(42, otherPersonDTO.getAge());
	}

	@Test
	public void testGetPersonsAndFirestationByAddress() throws IOException {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Doe");
		person.setPhone("111-222-3333");
		person.setAddress("123 Main St");

		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setFirstName("John");
		medicalRecord.setLastName("Doe");
		medicalRecord.setBirthdate("01/15/1980");
		medicalRecord.setMedications(Arrays.asList("Med1", "Med2"));
		medicalRecord.setAllergies(Arrays.asList("Allergy1", "Allergy2"));

		List<Person> persons = new ArrayList<>();
		persons.add(person);

		Firestation firestation = new Firestation();
		firestation.setStation(1);
		firestation.setAddress("123 Main St");

		when(personCRUD.findByAddress("123 Main St")).thenReturn(persons);
		when(firestationCRUD.findByAddress("123 Main St")).thenReturn(firestation);
		when(medicalRecordCRUD.findByFirstNameAndLastName("John", "Doe")).thenReturn(medicalRecord);
		when(medicalRecordService.calculateAge("01/15/1980")).thenReturn(42);

		ResidentsAndFirestationDTO result = personService.getResidentsAndFirestationByAddress("123 Main St");

		verify(personCRUD, times(1)).findByAddress("123 Main St");
		verify(firestationCRUD, times(1)).findByAddress("123 Main St");
		verify(medicalRecordCRUD, times(1)).findByFirstNameAndLastName("John", "Doe");
		verify(medicalRecordService, times(1)).calculateAge("01/15/1980");

		assertNotNull(result);

		List<ResidentsAndFirestationDTO.ResidentDTO> residents = result.getResidents();
		assertEquals(1, residents.size());

		ResidentsAndFirestationDTO.ResidentDTO residentDTO = residents.get(0);
		assertEquals("John", residentDTO.getFirstName());
		assertEquals("Doe", residentDTO.getLastName());
		assertEquals("111-222-3333", residentDTO.getPhone());
		assertEquals(42, residentDTO.getAge());
		assertEquals(Arrays.asList("Med1", "Med2"), residentDTO.getMedications());
		assertEquals(Arrays.asList("Allergy1", "Allergy2"), residentDTO.getAllergies());

		ResidentsAndFirestationDTO.FirestationInfoDTO firestationInfoDTO = result.getFirestationInfo();
		assertNotNull(firestationInfoDTO);
		assertEquals(1, firestationInfoDTO.getFirestationNumber());
	}

	@Test
	public void testGetPersonsAndFirestationByAddressNoPerson() throws IOException {
		when(personCRUD.findByAddress("123 Main St")).thenReturn(Arrays.asList());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> personService.getResidentsAndFirestationByAddress("123 Main St"));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("No residents found for this address"));

		verify(personCRUD, times(1)).findByAddress("123 Main St");
		verify(firestationCRUD, never()).findByAddress(any());
		verify(medicalRecordCRUD, never()).findByFirstNameAndLastName("John", "Doe");
		verify(medicalRecordService, never()).calculateAge(any());
	}

	@Test
	public void testGetPersonsAndFirestationByAddressNoFirestation() throws IOException {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Doe");
		person.setPhone("111-222-3333");
		person.setAddress("123 Main St");

		List<Person> persons = new ArrayList<>();
		persons.add(person);

		when(personCRUD.findByAddress("123 Main St")).thenReturn(persons);
		when(firestationCRUD.findByAddress("123 Main St")).thenReturn(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> personService.getResidentsAndFirestationByAddress("123 Main St"));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("No firestation found for this address"));

		verify(personCRUD, times(1)).findByAddress("123 Main St");
		verify(firestationCRUD, times(1)).findByAddress("123 Main St");
		verify(medicalRecordService, never()).calculateAge(any());
	}

	@Test
	public void testGetPersonInfoByFirstNameAndLastName() throws IOException {
		String firstName = "John";
		String lastName = "Doe";

		Person person = new Person();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setAddress("123 Main St");
		person.setEmail("john.doe@example.com");

		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setFirstName(firstName);
		medicalRecord.setLastName(lastName);
		medicalRecord.setBirthdate("01/15/1980");
		medicalRecord.setMedications(Arrays.asList("Med1", "Med2"));
		medicalRecord.setAllergies(Arrays.asList("Allergy1", "Allergy2"));

		when(personCRUD.findByFirstNameAndLastName(firstName, lastName)).thenReturn(person);
		when(medicalRecordCRUD.findByFirstNameAndLastName(firstName, lastName)).thenReturn(medicalRecord);
		when(medicalRecordService.calculateAge("01/15/1980")).thenReturn(42);

		PersonInfoDTO result = personService.getPersonInfoByFirstNameAndLastName(firstName, lastName);

		verify(personCRUD, times(1)).findByFirstNameAndLastName(firstName, lastName);
		verify(medicalRecordCRUD, times(2)).findByFirstNameAndLastName(firstName, lastName);
		verify(medicalRecordService, times(1)).calculateAge("01/15/1980");

		assertNotNull(result);

		assertEquals(firstName, result.getFirstName());
		assertEquals(lastName, result.getLastName());
		assertEquals("123 Main St", result.getAddress());
		assertEquals("john.doe@example.com", result.getEmail());
		assertEquals(42, result.getAge());
		assertEquals(Arrays.asList("Med1", "Med2"), result.getMedications());
		assertEquals(Arrays.asList("Allergy1", "Allergy2"), result.getAllergies());
	}

	@Test
	public void testGetPersonInfoByFirstNameAndLastNameNotFound() throws IOException {
		String firstName = "John";
		String lastName = "Doe";

		when(personCRUD.findByFirstNameAndLastName(firstName, lastName)).thenReturn(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> personService.getPersonInfoByFirstNameAndLastName(firstName, lastName));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This person doesn't exist"));

		verify(personCRUD, times(1)).findByFirstNameAndLastName(firstName, lastName);
		verify(medicalRecordService, never()).calculateAge(any());
		verify(medicalRecordCRUD, never()).findByFirstNameAndLastName(firstName, lastName);
	}

	@Test
	public void testGetCommunityEmails() throws IOException {
		String city = "Culver";

		Person person1 = new Person();
		person1.setFirstName("John");
		person1.setLastName("Doe");
		person1.setAddress("123 Main St");
		person1.setCity(city);
		person1.setEmail("test@test.com");
		person1.setPhone("111-222-3333");
		person1.setZip("12345");

		Person person2 = new Person();
		person2.setFirstName("Jane");
		person2.setLastName("Smith");
		person2.setAddress("456 Main St");
		person2.setCity(city);
		person2.setPhone("444-555-6666");
		person2.setZip("54321");
		person2.setEmail("jane.smith@example.com");

		List<Person> residents = Arrays.asList(person1, person2);

		when(personCRUD.findByCity(city)).thenReturn(residents);

		CommunityEmailsDTO result = personService.getCommunityEmails(city);

		assertNotNull(result);

		List<String> expectedEmails = Arrays.asList("test@test.com", "jane.smith@example.com");
		assertEquals(expectedEmails, result.getEmails());
	}

	@Test
	public void testGetCommunityEmailsNoPerson() throws IOException {
		String city = "Culver";

		when(personCRUD.findByCity(city)).thenReturn(Arrays.asList());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> personService.getCommunityEmails(city));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("No residents found for the city"));

		verify(personCRUD, times(1)).findByCity(city);
	}

}
