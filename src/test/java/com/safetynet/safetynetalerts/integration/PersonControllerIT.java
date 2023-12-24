package com.safetynet.safetynetalerts.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.FirestationRepository;
import com.safetynet.safetynetalerts.repository.MedicalRecordRepository;
import com.safetynet.safetynetalerts.repository.PersonRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driverClassName=org.h2.Driver", "spring.datasource.username=sa",
		"spring.datasource.password=password", "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect" })
public class PersonControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FirestationRepository firestationRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private MedicalRecordRepository medicalRecordRepository;

	private static Firestation TEST_FIRESTATION;

	private static Person TEST_PERSON;

	private static MedicalRecord TEST_MEDICALRECORD;

	private static Person TEST_CHILD;

	private static MedicalRecord TEST_MEDICALRECORDCHILD;

	@BeforeEach
	public void setUpPerTest() {
		firestationRepository.deleteAll();
		personRepository.deleteAll();
		medicalRecordRepository.deleteAll();

		TEST_FIRESTATION = new Firestation();
		TEST_FIRESTATION.setAddress("1509 Culver St");
		TEST_FIRESTATION.setStation(1);
		firestationRepository.save(TEST_FIRESTATION);

		TEST_PERSON = new Person();
		TEST_PERSON.setFirstName("John");
		TEST_PERSON.setLastName("Boyd");
		TEST_PERSON.setAddress("1509 Culver St");
		TEST_PERSON.setCity("Culver");
		TEST_PERSON.setZip("97451");
		TEST_PERSON.setPhone("841-874-6512");
		TEST_PERSON.setEmail("test1@test.com");
		personRepository.save(TEST_PERSON);

		TEST_MEDICALRECORD = new MedicalRecord();
		TEST_MEDICALRECORD.setPerson(TEST_PERSON);
		TEST_MEDICALRECORD.setBirthdate("03/06/1984");
		TEST_MEDICALRECORD.setMedications(List.of("aznol:350mg", "hydrapermazol:100mg"));
		TEST_MEDICALRECORD.setAllergies(List.of("nillacilan"));
		medicalRecordRepository.save(TEST_MEDICALRECORD);

		TEST_CHILD = new Person();
		TEST_CHILD.setFirstName("Danis");
		TEST_CHILD.setLastName("Boyd");
		TEST_CHILD.setAddress("1509 Culver St");
		TEST_CHILD.setCity("Culver");
		TEST_CHILD.setZip("97451");
		TEST_CHILD.setPhone("841-874-6512");
		TEST_CHILD.setEmail("test2@test.com");
		personRepository.save(TEST_CHILD);

		TEST_MEDICALRECORDCHILD = new MedicalRecord();
		TEST_MEDICALRECORDCHILD.setPerson(TEST_CHILD);
		TEST_MEDICALRECORDCHILD.setBirthdate("03/06/2010");
		TEST_MEDICALRECORDCHILD.setMedications(List.of("aznol:350mg", "hydrapermazol:100mg"));
		TEST_MEDICALRECORDCHILD.setAllergies(List.of("nillacilan"));
		medicalRecordRepository.save(TEST_MEDICALRECORDCHILD);

	}

	@AfterEach
	public void tearDown() {
		firestationRepository.deleteAll();
		personRepository.deleteAll();
		medicalRecordRepository.deleteAll();
	}

	@Test
	public void testGetChildsByAddress() throws Exception {
		mockMvc.perform(get("/childAlert").param("address", "1509 Culver St").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].firstName").value("Danis"))
				.andExpect(jsonPath("$[0].lastName").value("Boyd")).andExpect(jsonPath("$[0].age").value(13))
				.andExpect(jsonPath("$[0].otherMembers[0].firstName").value("John"))
				.andExpect(jsonPath("$[0].otherMembers[0].lastName").value("Boyd"));
	}

	@Test
	public void testGetPersonsAndFirestationByAddress() throws Exception {
		mockMvc.perform(get("/fire").param("address", "1509 Culver St").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].firstName").value("John"))
				.andExpect(jsonPath("$[0].lastName").value("Boyd"))
				.andExpect(jsonPath("$[0].phone").value("841-874-6512")).andExpect(jsonPath("$[0].age").value(39))
				.andExpect(jsonPath("$[1].firstName").value("Danis")).andExpect(jsonPath("$[1].lastName").value("Boyd"))
				.andExpect(jsonPath("$[1].phone").value("841-874-6512")).andExpect(jsonPath("$[1].age").value(13))
				.andExpect(jsonPath("$[2].firestationNumber").value(1));
	}

	@Test
	public void testGetPersonInfoByFirstNameAndLastName() throws Exception {
		mockMvc.perform(get("/personInfo").param("firstName", "John")
				.param("lastName", "Boyd").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].firstName").value("John")).andExpect(jsonPath("$[0].lastName").value("Boyd"))
				.andExpect(jsonPath("$[0].age").value(39)).andExpect(jsonPath("$[0].address").value("1509 Culver St"))
				.andExpect(jsonPath("$[0].email").value("test1@test.com"))
				.andExpect(jsonPath("$[0].medications[0]").value("aznol:350mg"))
				.andExpect(jsonPath("$[0].medications[1]").value("hydrapermazol:100mg"))
				.andExpect(jsonPath("$[0].allergies[0]").value("nillacilan"));
	}
	
	@Test
	public void testGetCommunityEmails() throws Exception {
		mockMvc.perform(get("/communityEmail").param("city", "Culver").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andExpect(jsonPath("$[0]").value("test1@test.com"))
		.andExpect(jsonPath("$[1]").value("test2@test.com"));
	}
}