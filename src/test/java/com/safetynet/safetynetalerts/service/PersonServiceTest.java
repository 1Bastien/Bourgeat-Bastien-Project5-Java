package com.safetynet.safetynetalerts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

	@InjectMocks
	private PersonService personService;

	@Mock
	private static PersonRepository personRepository;

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
		when(personRepository.findByFirstNameAndLastName(eq("John"), eq("Boyd")))
				.thenReturn(Optional.empty());
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
	public void testDeletePerson() {
		when(personRepository.findByFirstNameAndLastName(eq("John"), eq("Boyd"))).thenReturn(Optional.of(TEST_PERSON));

		String result = personService.deletePerson("John", "Boyd");

		verify(personRepository, times(1)).findByFirstNameAndLastName("John", "Boyd");
		verify(personRepository, times(1)).delete(TEST_PERSON);

		assertNotNull(result);
		assertEquals("Person deleted", result);
	}
}
