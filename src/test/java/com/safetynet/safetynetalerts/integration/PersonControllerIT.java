package com.safetynet.safetynetalerts.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Arrays;
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
import com.safetynet.safetynetalerts.DTO.CommunityEmailsDTO;
import com.safetynet.safetynetalerts.DTO.ListChildDTO;
import com.safetynet.safetynetalerts.DTO.PersonInfoDTO;
import com.safetynet.safetynetalerts.DTO.ResidentsAndFirestationDTO;
import com.safetynet.safetynetalerts.configuration.DataStore;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PersonControllerIT {

	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	private DataStore dataStore;

	@Autowired
	public PersonControllerIT(MockMvc mockMvc, ObjectMapper objectMapper, DataStore dataStore) {
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
	public void testGetChildsByAddress() throws Exception {
		List<Person> persons = dataStore.getPersons();
		Person child1 = new Person();
		child1.setFirstName("John");
		child1.setLastName("Doe");
		child1.setAddress("123 Main St");
		persons.add(child1);

		Person otherMember = new Person();
		otherMember.setFirstName("Jane");
		otherMember.setLastName("Smith");
		otherMember.setAddress("123 Main St");
		persons.add(otherMember);

		List<MedicalRecord> medicalRecords = dataStore.getMedicalrecords();
		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setFirstName("John");
		medicalRecord.setLastName("Doe");
		medicalRecord.setBirthdate("01/01/2009");
		medicalRecords.add(medicalRecord);

		MedicalRecord medicalRecord2 = new MedicalRecord();
		medicalRecord2.setFirstName("Jane");
		medicalRecord2.setLastName("Smith");
		medicalRecord2.setBirthdate("01/01/1979");
		medicalRecords.add(medicalRecord2);

		dataStore.saveToFile(objectMapper);

		MvcResult result = mockMvc
				.perform(get("/childAlert?address=123 Main St").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		ListChildDTO listChildDTO = objectMapper.readValue(result.getResponse().getContentAsString(),
				ListChildDTO.class);
		assertNotNull(listChildDTO);

		List<ListChildDTO.ChildDTO> children = listChildDTO.getChildren();
		assertNotNull(children);

		assertEquals(1, children.size());
		assertEquals("John", children.get(0).getFirstName());
		assertEquals("Doe", children.get(0).getLastName());
		assertEquals(15, children.get(0).getAge());
		assertNotNull(children.get(0).getOtherMembers());
		assertEquals(1, children.get(0).getOtherMembers().size());
		assertEquals("Jane", children.get(0).getOtherMembers().get(0).getFirstName());
		assertEquals("Smith", children.get(0).getOtherMembers().get(0).getLastName());
		assertEquals(45, children.get(0).getOtherMembers().get(0).getAge());
	}

	@Test
	public void testGetPersonsAndFirestationByAddress() throws Exception {
		List<Person> persons = dataStore.getPersons();
		Person resident1 = new Person();
		resident1.setFirstName("John");
		resident1.setLastName("Doe");
		resident1.setAddress("123 Main St");
		resident1.setPhone("123-456-7890");
		persons.add(resident1);

		List<MedicalRecord> medicalRecords = dataStore.getMedicalrecords();
		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setFirstName("John");
		medicalRecord.setLastName("Doe");
		medicalRecord.setBirthdate("01/01/1989");
		medicalRecords.add(medicalRecord);

		List<Firestation> firestations = dataStore.getFirestations();
		Firestation firestation = new Firestation();
		firestation.setStation(1);
		firestation.setAddress("123 Main St");
		firestations.add(firestation);

		dataStore.saveToFile(objectMapper);

		MvcResult result = mockMvc.perform(get("/fire?address=123 Main St").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		ResidentsAndFirestationDTO residentsAndFirestationDTO = objectMapper
				.readValue(result.getResponse().getContentAsString(), ResidentsAndFirestationDTO.class);
		assertNotNull(residentsAndFirestationDTO);

		List<ResidentsAndFirestationDTO.ResidentDTO> residents = residentsAndFirestationDTO.getResidents();
		assertNotNull(residents);

		ResidentsAndFirestationDTO.FirestationInfoDTO firestationInfo = residentsAndFirestationDTO.getFirestationInfo();
		assertNotNull(firestationInfo);

		assertEquals(1, residents.size());
		assertEquals("John", residents.get(0).getFirstName());
		assertEquals("Doe", residents.get(0).getLastName());
		assertEquals("123-456-7890", residents.get(0).getPhone());
		assertEquals(35, residents.get(0).getAge());

		assertEquals(1, firestationInfo.getFirestationNumber());
	}

	@Test
	public void testGetPersonInfoByFirstNameAndLastName() throws Exception {
		List<Person> persons = dataStore.getPersons();
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Doe");
		person.setAddress("123 Main St");
		person.setEmail("john.doe@example.com");
		person.setPhone("123-456-7890");
		persons.add(person);

		List<MedicalRecord> medicalRecords = dataStore.getMedicalrecords();
		MedicalRecord medicalRecord = new MedicalRecord();
		medicalRecord.setFirstName("John");
		medicalRecord.setLastName("Doe");
		medicalRecord.setBirthdate("01/01/1990");
		medicalRecord.setMedications(Arrays.asList("Med1", "Med2"));
		medicalRecord.setAllergies(Arrays.asList("Allergy1", "Allergy2"));
		medicalRecords.add(medicalRecord);

		dataStore.saveToFile(objectMapper);

		MvcResult result = mockMvc.perform(get("/personInfo").param("firstName", "John").param("lastName", "Doe")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		PersonInfoDTO personInfoDTO = objectMapper.readValue(result.getResponse().getContentAsString(),
				PersonInfoDTO.class);
		assertNotNull(personInfoDTO);

		assertEquals("John", personInfoDTO.getFirstName());
		assertEquals("Doe", personInfoDTO.getLastName());
		assertEquals("123 Main St", personInfoDTO.getAddress());
		assertEquals("john.doe@example.com", personInfoDTO.getEmail());
		assertEquals(34, personInfoDTO.getAge());
		assertNotNull(personInfoDTO.getMedications());
		assertNotNull(personInfoDTO.getAllergies());
	}

	@Test
	public void testGetCommunityEmails() throws Exception {
		List<Person> persons = dataStore.getPersons();
		Person person1 = new Person();
		person1.setFirstName("John");
		person1.setLastName("Doe");
		person1.setAddress("123 Main St");
		person1.setEmail("john.doe@example.com");
		person1.setCity("Culver");
		persons.add(person1);

		Person person2 = new Person();
		person2.setFirstName("Jane");
		person2.setLastName("Smith");
		person2.setAddress("456 Oak St");
		person2.setEmail("jane.smith@example.com");
		person2.setCity("Culver");
		persons.add(person2);

		Person person3 = new Person();
		person3.setFirstName("Bob");
		person3.setLastName("Johnson");
		person3.setAddress("789 Maple St");
		person3.setEmail("bob.johnson@example.com");
		person3.setCity("Springfield");
		persons.add(person3);

		dataStore.saveToFile(objectMapper);

		MvcResult result = mockMvc.perform(get("/communityEmail?city=Culver").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		CommunityEmailsDTO communityEmailsDTO = objectMapper.readValue(result.getResponse().getContentAsString(),
				CommunityEmailsDTO.class);
		assertNotNull(communityEmailsDTO);

		List<String> emails = communityEmailsDTO.getEmails();
		assertNotNull(emails);
		assertEquals(2, emails.size());
		assertEquals("john.doe@example.com", emails.get(0));
		assertEquals("jane.smith@example.com", emails.get(1));
	}
}