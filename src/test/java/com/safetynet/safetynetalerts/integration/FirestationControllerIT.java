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
public class FirestationControllerIT {

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
	}

	@AfterEach
	public void tearDown() {
		firestationRepository.deleteAll();
		personRepository.deleteAll();
		medicalRecordRepository.deleteAll();
	}

	@Test
	public void testGetListOfPersonByFirestation() throws Exception {
		mockMvc.perform(get("/firestation?stationNumber=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].firstName").value("John"))
				.andExpect(jsonPath("$[0].lastName").value("Boyd"))
				.andExpect(jsonPath("$[0].address").value("1509 Culver St"))
				.andExpect(jsonPath("$[0].phone").value("841-874-6512")).andExpect(jsonPath("$[1]").exists())
				.andExpect(jsonPath("$[1].adultCount").exists()).andExpect(jsonPath("$[1].childCount").exists())
				.andExpect(jsonPath("$[1].adultCount").value(1)).andExpect(jsonPath("$[1].childCount").value(0));
	}

	@Test
	public void testGetPhoneNumbersByFirestation() throws Exception {
		Person person2 = new Person();
		person2.setFirstName("Jacob");
		person2.setLastName("Doe");
		person2.setAddress("2312 Culver St");
		person2.setCity("Culver");
		person2.setZip("97451");
		person2.setPhone("123-456-7890");
		person2.setEmail("test@test2.com");

		personRepository.save(person2);

		Firestation firestation2 = new Firestation();
		firestation2.setAddress("2312 Culver St");
		firestation2.setStation(1);

		firestationRepository.save(firestation2);

		mockMvc.perform(get("/phoneAlert?firestation=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0]").value("841-874-6512"))
				.andExpect(jsonPath("$[1]").value("123-456-7890"));
	}

	@Test
	public void testGetPersonsByStations() throws Exception {
		mockMvc.perform(get("/flood?stations=1,2").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].firstName").value("John")).andExpect(jsonPath("$[0].lastName").value("Boyd"))
				.andExpect(jsonPath("$[0].age").value("39"))
				.andExpect(jsonPath("$[0].medications[0]").value("aznol:350mg"))
				.andExpect(jsonPath("$[0].medications[1]").value("hydrapermazol:100mg"))
				.andExpect(jsonPath("$[0].allergies[0]").value("nillacilan"));
	}
}
