package com.safetynet.safetynetalerts.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.DTO.FirestationInfoDTO;
import com.safetynet.safetynetalerts.DTO.PersonInfoDTO;
import com.safetynet.safetynetalerts.DTO.PersonsByStationsDTO;
import com.safetynet.safetynetalerts.DTO.PhoneNumbersDTO;
import com.safetynet.safetynetalerts.configuration.DataStore;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FirestationControllerIT {

	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	private DataStore dataStore;

	@Autowired
	public FirestationControllerIT(MockMvc mockMvc, ObjectMapper objectMapper, DataStore dataStore) {
		this.mockMvc = mockMvc;
		this.objectMapper = objectMapper;
		this.dataStore = dataStore;
	}

	@BeforeEach
	public void beforeTest() throws IOException {
		List<Firestation> firestations = dataStore.getFirestations();
		List<Person> persons = dataStore.getPersons();
		List<MedicalRecord> medicalRecords = dataStore.getMedicalrecords();

		firestations.clear();
		persons.clear();
		medicalRecords.clear();

		dataStore.saveToFile(objectMapper);
	}

	@AfterEach
	public void afterTest() throws IOException {
		List<Firestation> firestations = dataStore.getFirestations();
		List<Person> persons = dataStore.getPersons();
		List<MedicalRecord> medicalRecords = dataStore.getMedicalrecords();

		firestations.clear();
		persons.clear();
		medicalRecords.clear();

		dataStore.saveToFile(objectMapper);
	}

	@Test
	public void testGetListOfPersonByFirestation() throws Exception {
		List<Firestation> firestations = dataStore.getFirestations();
		List<Person> persons = dataStore.getPersons();
		List<MedicalRecord> medicalRecords = dataStore.getMedicalrecords();

		Firestation firestation = new Firestation();
		firestation.setAddress("123 Main St");
		firestation.setStation(1);
		firestations.add(firestation);

		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Boyd");
		person.setAddress("123 Main St");
		person.setPhone("123-456-7890");
		person.setCity("Culver");
		person.setZip("97451");
		person.setEmail("test@test.com");
		persons.add(person);

		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setFirstName("John");
		medicalRecord.setLastName("Boyd");
		medicalRecord.setBirthdate("03/06/1995");
		medicalRecords.add(medicalRecord);

		dataStore.saveToFile(objectMapper);

		MvcResult result = mockMvc.perform(get("/firestation?stationNumber=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		FirestationInfoDTO firestationInfoDTO = objectMapper.readValue(result.getResponse().getContentAsString(),
				FirestationInfoDTO.class);

		assertNotNull(firestationInfoDTO);
		assertEquals(1, firestationInfoDTO.getAdultCount());
		assertEquals(0, firestationInfoDTO.getChildCount());
		assertEquals("John", firestationInfoDTO.getResidents().get(0).getFirstName());
		assertEquals("Boyd", firestationInfoDTO.getResidents().get(0).getLastName());
		assertEquals("123 Main St", firestationInfoDTO.getResidents().get(0).getAddress());
		assertEquals("123-456-7890", firestationInfoDTO.getResidents().get(0).getPhone());

	}

	@Test
	public void testGetPhoneNumbersByFirestation() throws Exception {
		List<Firestation> firestations = dataStore.getFirestations();
		Firestation firestation = new Firestation();
		firestation.setAddress("123 Main St");
		firestation.setStation(1);
		firestations.add(firestation);

		List<Person> persons = dataStore.getPersons();
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Doe");
		person.setAddress("123 Main St");
		person.setPhone("555-1234");
		persons.add(person);

		dataStore.saveToFile(objectMapper);

		MvcResult result = mockMvc.perform(get("/phoneAlert?firestation=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		PhoneNumbersDTO phoneNumbersDTO = objectMapper.readValue(result.getResponse().getContentAsString(),
				PhoneNumbersDTO.class);
		assertNotNull(phoneNumbersDTO);
		List<String> phoneNumbers = phoneNumbersDTO.getPhoneNumbers();
		assertNotNull(phoneNumbers);

		assertEquals(1, phoneNumbers.size());
		assertEquals("555-1234", phoneNumbers.get(0));
	}

	@Test
	public void testGetPersonsByStations() throws Exception {
		List<Firestation> firestations = dataStore.getFirestations();
		Firestation firestation1 = new Firestation();
		firestation1.setAddress("123 Main St");
		firestation1.setStation(1);
		firestations.add(firestation1);

		Firestation firestation2 = new Firestation();
		firestation2.setAddress("456 Oak St");
		firestation2.setStation(2);
		firestations.add(firestation2);

		List<Person> persons = dataStore.getPersons();
		Person person1 = new Person();
		person1.setFirstName("John");
		person1.setLastName("Doe");
		person1.setAddress("123 Main St");
		person1.setCity("Culver");
		person1.setZip("97451");
		person1.setPhone("555-1234");
		person1.setEmail("test@test.com");
		persons.add(person1);

		Person person2 = new Person();
		person2.setFirstName("Jane");
		person2.setLastName("Smith");
		person2.setAddress("456 Oak St");
		person2.setCity("Culver");
		person2.setZip("97451");
		person2.setPhone("555-5678");
		person2.setEmail("test@test.com");
		persons.add(person2);
		
		List<MedicalRecord> medicalRecords = dataStore.getMedicalrecords();
		MedicalRecord medicalRecord1 = new MedicalRecord();
		medicalRecord1.setFirstName("John");
		medicalRecord1.setLastName("Doe");
		medicalRecord1.setBirthdate("03/06/1995");
		medicalRecords.add(medicalRecord1);
		
		MedicalRecord medicalRecord2 = new MedicalRecord();
		medicalRecord2.setFirstName("Jane");
		medicalRecord2.setLastName("Smith");
		medicalRecord2.setBirthdate("03/06/1995");
		medicalRecords.add(medicalRecord2);

		dataStore.saveToFile(objectMapper);

		MvcResult result = mockMvc.perform(get("/flood/stations?stations=1,2").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		PersonsByStationsDTO personsByStationsDTO = objectMapper.readValue(result.getResponse().getContentAsString(),
				PersonsByStationsDTO.class);
		assertNotNull(personsByStationsDTO);
		List<PersonInfoDTO> personInfoDTOs = personsByStationsDTO.getPersonsByStations();
		assertNotNull(personInfoDTOs);

		assertEquals(2, personInfoDTOs.size());
		assertEquals("John", personInfoDTOs.get(0).getFirstName());
		assertEquals("Doe", personInfoDTOs.get(0).getLastName());
		assertEquals("Jane", personInfoDTOs.get(1).getFirstName());
		assertEquals("Smith", personInfoDTOs.get(1).getLastName());
	}

}
