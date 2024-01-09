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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.safetynet.safetynetalerts.DTO.FirestationInfoDTO;
import com.safetynet.safetynetalerts.DTO.PersonInfoDTO;
import com.safetynet.safetynetalerts.DTO.PersonsByStationsDTO;
import com.safetynet.safetynetalerts.DTO.PhoneNumbersDTO;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;

@ExtendWith(MockitoExtension.class)
public class FirestationServiceTest {

	@InjectMocks
	private FirestationService firestationService;

	@Mock
	private static FirestationCRUD firestationCRUD;

	@Mock
	private static PersonCRUD personCRUD;

	@Mock
	private static MedicalRecordCRUD medicalRecordCRUD;

	@Mock
	private static MedicalRecordService medicalRecordService;

	@Test
	public void testPostFirestation() throws IOException {
		Firestation firestation = new Firestation();
		firestation.setAddress("1509 Culver St");
		firestation.setStation(3);

		when(firestationCRUD.findByAddress(eq("1509 Culver St"))).thenReturn(null);
		when(firestationCRUD.save(firestation)).thenReturn(firestation);

		Firestation result = firestationService.postFirestation(firestation);

		verify(firestationCRUD, times(1)).findByAddress("1509 Culver St");
		verify(firestationCRUD, times(1)).save(firestation);

		assertNotNull(result);
		assertEquals("1509 Culver St", result.getAddress());
		assertEquals(3, result.getStation());
	}

	@Test
	public void testPostFirestationAlreadyExist() throws IOException {
		Firestation firestation = new Firestation();
		firestation.setAddress("1509 Culver St");
		firestation.setStation(3);

		when(firestationCRUD.findByAddress("1509 Culver St")).thenReturn(firestation);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> firestationService.postFirestation(firestation));

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
		assertTrue(exception.getReason().contains("Firestation is already assigned for this address"));
		assertTrue(exception.getReason().contains(firestation.getAddress()));

		verify(firestationCRUD, times(1)).findByAddress(firestation.getAddress());
		verify(firestationCRUD, never()).save(firestation);
	}

	@Test
	public void testPutFirestation() throws IOException {
		Firestation newFirestation = new Firestation();
		newFirestation.setAddress("1509 Culver St");
		newFirestation.setStation(4);

		Firestation oldFirestation = new Firestation();
		oldFirestation.setAddress("1509 Culver St");
		oldFirestation.setStation(3);

		when(firestationCRUD.findByAddress("1509 Culver St")).thenReturn(oldFirestation);
		when(firestationCRUD.save(oldFirestation)).thenReturn(oldFirestation);

		Firestation result = firestationService.putFirestation("1509 Culver St", newFirestation);

		verify(firestationCRUD, times(1)).findByAddress("1509 Culver St");
		verify(firestationCRUD, times(1)).save(oldFirestation);

		assertNotNull(result);
		assertEquals(4, result.getStation());
	}

	@Test
	public void testPutFirestationNotFound() throws IOException {
		Firestation newFirestation = new Firestation();
		newFirestation.setAddress("1509 Culver St");
		newFirestation.setStation(4);

		when(firestationCRUD.findByAddress("1509 Culver St")).thenReturn(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> firestationService.putFirestation("1509 Culver St", newFirestation));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This firestation doesn't exist"));

		verify(firestationCRUD, times(1)).findByAddress("1509 Culver St");
		verify(firestationCRUD, never()).save(any());
	}

	@Test
	public void testDeleteFirestation() throws IOException {
		Firestation firestation = new Firestation();
		firestation.setAddress("1509 Culver St");
		firestation.setStation(3);

		when(firestationCRUD.findByAddress("1509 Culver St")).thenReturn(firestation);

		String result = firestationService.deleteFirestation("1509 Culver St");

		verify(firestationCRUD, times(1)).findByAddress("1509 Culver St");
		verify(firestationCRUD, times(1)).delete(firestation);

		assertNotNull(result);
		assertEquals("Firestation object deleted", result);
	}

	@Test
	public void testDeleteFirestationNotFound() throws IOException {
		when(firestationCRUD.findByAddress("1509 Culver St")).thenReturn(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> firestationService.deleteFirestation("1509 Culver St"));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("This firestation doesn't exist"));

		verify(firestationCRUD, times(1)).findByAddress("1509 Culver St");
		verify(firestationCRUD, never()).delete(any());
	}

	@Test
	public void testGetListOfPersonByFirestation() throws IOException {
		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setFirstName("John");
		medicalRecord.setLastName("Boyd");
		medicalRecord.setBirthdate("03/06/1995");

		List<Firestation> firestations = new ArrayList<>();
		Firestation firestation = new Firestation();
		firestation.setAddress("123 Main St");
		firestation.setStation(1);
		firestations.add(firestation);

		List<Person> residents = new ArrayList<>();
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Boyd");
		person.setAddress("123 Main St");
		person.setPhone("123-456-7890");
		person.setCity("Culver");
		person.setZip("97451");
		person.setEmail("test@test.com");
		residents.add(person);

		when(firestationCRUD.findAllByStation(anyInt())).thenReturn(firestations);
		when(personCRUD.findByAddress("123 Main St")).thenReturn(residents);
		when(medicalRecordCRUD.findByFirstNameAndLastName("John", "Boyd")).thenReturn(medicalRecord);
		when(medicalRecordService.calculateAge(anyString())).thenReturn(28);

		FirestationInfoDTO firestationInfoDTO = firestationService.getListOfPersonByFirestation(1);

		verify(firestationCRUD, times(1)).findAllByStation(1);
		verify(personCRUD, times(1)).findByAddress("123 Main St");
		verify(medicalRecordCRUD, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(medicalRecordService, times(1)).calculateAge("03/06/1995");

		assertNotNull(firestationInfoDTO);
		assertEquals(1, firestationInfoDTO.getResidents().size());
		assertEquals(1, firestationInfoDTO.getAdultCount());
		assertEquals(0, firestationInfoDTO.getChildCount());
		assertEquals("John", firestationInfoDTO.getResidents().get(0).getFirstName());
		assertEquals("Boyd", firestationInfoDTO.getResidents().get(0).getLastName());
		assertEquals("123 Main St", firestationInfoDTO.getResidents().get(0).getAddress());
		assertEquals("123-456-7890", firestationInfoDTO.getResidents().get(0).getPhone());
	}

	@Test
	public void testGetListOfPersonByFirestationWhenChild() throws IOException {

		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setFirstName("Marc");
		medicalRecord.setLastName("Boyd");
		medicalRecord.setBirthdate("03/06/2010");

		List<Firestation> firestations = new ArrayList<>();
		Firestation firestation = new Firestation();
		firestation.setAddress("123 Main St");
		firestation.setStation(1);
		firestations.add(firestation);

		List<Person> residents = new ArrayList<>();
		Person person = new Person();
		person.setFirstName("Marc");
		person.setLastName("Boyd");
		person.setAddress("123 Main St");
		person.setPhone("123-456-7890");
		person.setCity("Culver");
		person.setZip("97451");
		person.setEmail("test@test.com");
		residents.add(person);

		when(firestationCRUD.findAllByStation(anyInt())).thenReturn(firestations);
		when(personCRUD.findByAddress("123 Main St")).thenReturn(residents);
		when(medicalRecordCRUD.findByFirstNameAndLastName("Marc", "Boyd")).thenReturn(medicalRecord);
		when(medicalRecordService.calculateAge(anyString())).thenReturn(13);

		FirestationInfoDTO firestationInfoDTO = firestationService.getListOfPersonByFirestation(1);

		verify(firestationCRUD, times(1)).findAllByStation(1);
		verify(personCRUD, times(1)).findByAddress("123 Main St");
		verify(medicalRecordCRUD, times(1)).findByFirstNameAndLastName("Marc", "Boyd");
		verify(medicalRecordService, times(1)).calculateAge("03/06/2010");

		assertNotNull(firestationInfoDTO);
		assertEquals(1, firestationInfoDTO.getResidents().size());
		assertEquals(0, firestationInfoDTO.getAdultCount());
		assertEquals(1, firestationInfoDTO.getChildCount());
		assertEquals("Marc", firestationInfoDTO.getResidents().get(0).getFirstName());
		assertEquals("Boyd", firestationInfoDTO.getResidents().get(0).getLastName());
		assertEquals("123 Main St", firestationInfoDTO.getResidents().get(0).getAddress());
		assertEquals("123-456-7890", firestationInfoDTO.getResidents().get(0).getPhone());
	}

	@Test
	public void testGetListOfPersonByFirestationNotFound() throws IOException {
		when(firestationCRUD.findAllByStation(anyInt())).thenReturn(Collections.emptyList());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> firestationService.getListOfPersonByFirestation(3));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("No firestation found for this number"));

		verify(firestationCRUD, times(1)).findAllByStation(3);
		verify(personCRUD, never()).findByAddress(anyString());
		verify(medicalRecordService, never()).calculateAge(anyString());
	}

	@Test
	public void testGetPhoneNumbersByFirestation() throws IOException {
		List<Firestation> firestations = new ArrayList<>();
		Firestation firestation = new Firestation();
		firestation.setAddress("123 Main St");
		firestation.setStation(1);
		firestations.add(firestation);

		List<Person> residents = new ArrayList<>();
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Boyd");
		person.setAddress("123 Main St");
		person.setPhone("123-456-7890");
		person.setCity("Culver");
		person.setZip("97451");
		person.setEmail("test@test.com");
		residents.add(person);

		when(firestationCRUD.findAllByStation(1)).thenReturn(firestations);
		when(personCRUD.findByAddress("123 Main St")).thenReturn(residents);

		PhoneNumbersDTO result = firestationService.getPhoneNumbersByFirestation(1);

		verify(firestationCRUD, times(1)).findAllByStation(1);
		verify(personCRUD, times(1)).findByAddress("123 Main St");

		assertNotNull(result);
		assertEquals(1, result.getPhoneNumbers().size());
		assertEquals("123-456-7890", result.getPhoneNumbers().get(0));
	}

	@Test
	public void testGetPhoneNumbersByFirestationNotFound() throws IOException {
		when(firestationCRUD.findAllByStation(anyInt())).thenReturn(Collections.emptyList());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> firestationService.getPhoneNumbersByFirestation(1));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("No firestation found for this number"));

		verify(firestationCRUD, times(1)).findAllByStation(1);
		verify(personCRUD, never()).findByAddress(anyString());
	}

	@Test
	public void testGetPersonsByStations() throws IOException {
		List<Firestation> firestations = new ArrayList<>();
		Firestation firestation1 = new Firestation();
		firestation1.setStation(1);
		firestation1.setAddress("123 Main St");

		firestations.add(firestation1);

		List<Person> address1 = new ArrayList<>();
		Person person1 = new Person();
		person1.setFirstName("John");
		person1.setLastName("Doe");
		person1.setAddress("123 Main St");
		person1.setPhone("123-456-7890");

		MedicalRecord medicalRecord1 = new MedicalRecord();
		medicalRecord1.setFirstName("John");
		medicalRecord1.setLastName("Doe");
		medicalRecord1.setBirthdate("03/06/1995");

		address1.add(person1);

		when(firestationCRUD.findAllByStation(1)).thenReturn(firestations);

		when(personCRUD.findByAddress("123 Main St")).thenReturn(address1);

		when(medicalRecordCRUD.findByFirstNameAndLastName("John", "Doe")).thenReturn(medicalRecord1);
		when(medicalRecordService.calculateAge("03/06/1995")).thenReturn(28);

		PersonsByStationsDTO result = firestationService.getPersonsByStations(Arrays.asList(1));

		assertNotNull(result);
		List<PersonInfoDTO> personInfoDTOList = result.getPersonsByStations();
		assertEquals(1, personInfoDTOList.size());

		PersonInfoDTO personInfoDTO1 = personInfoDTOList.get(0);
		assertEquals("123 Main St", personInfoDTO1.getAddress());
		assertEquals("John", personInfoDTO1.getFirstName());
		assertEquals("Doe", personInfoDTO1.getLastName());
		assertEquals("123-456-7890", personInfoDTO1.getPhone());
		assertEquals(28, personInfoDTO1.getAge());
	}

}
