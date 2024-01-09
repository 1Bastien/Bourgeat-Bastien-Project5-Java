package com.safetynet.safetyalerts.CRUD;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.CRUD.PersonCrudImpl;
import com.safetynet.safetynetalerts.configuration.DataStore;
import com.safetynet.safetynetalerts.model.Person;

@ExtendWith(MockitoExtension.class)
public class PersonCrudImplTest {

	@Mock
	private DataStore dataStore;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private PersonCrudImpl personCrudImpl;

	@Test
	public void testSavePerson() throws IOException {
		Person personToSave = new Person();
		personToSave.setFirstName("John");
		personToSave.setLastName("Doe");

		List<Person> currentPersons = new ArrayList<>();

		when(dataStore.getPersons()).thenReturn(currentPersons);

		Mockito.doNothing().when(dataStore).saveToFile(objectMapper);

		Person savedPerson = personCrudImpl.save(personToSave);

		assertEquals(1, currentPersons.size());
		assertEquals(personToSave, currentPersons.get(0));

		verify(dataStore, times(1)).saveToFile(objectMapper);

		assertEquals(personToSave, savedPerson);
	}

	@Test
	public void testFindByFirstNameAndLastName() throws IOException {
		Person testPerson = new Person();
		testPerson.setFirstName("John");
		testPerson.setLastName("Doe");

		List<Person> currentPersons = new ArrayList<>();
		currentPersons.add(testPerson);

		when(dataStore.getPersons()).thenReturn(currentPersons);

		Person foundPerson = personCrudImpl.findByFirstNameAndLastName("John", "Doe");

		assertNotNull(foundPerson);
		assertEquals(testPerson, foundPerson);
	}

	@Test
	public void testFindByAddress() throws IOException {
		String testAddress = "123 Main St";

		Person testPerson = new Person();
		testPerson.setFirstName("John");
		testPerson.setLastName("Doe");
		testPerson.setAddress(testAddress);

		List<Person> currentPersons = new ArrayList<>();
		currentPersons.add(testPerson);

		when(dataStore.getPersons()).thenReturn(currentPersons);

		List<Person> foundPersons = personCrudImpl.findByAddress(testAddress);

		assertNotNull(foundPersons);
		assertEquals(1, foundPersons.size());
		assertEquals(testPerson, foundPersons.get(0));
	}

	@Test
	public void testFindByCity() throws IOException {
		String testCity = "Culver";

		Person testPerson = new Person();
		testPerson.setFirstName("John");
		testPerson.setLastName("Doe");
		testPerson.setCity(testCity);

		List<Person> currentPersons = new ArrayList<>();
		currentPersons.add(testPerson);

		when(dataStore.getPersons()).thenReturn(currentPersons);

		List<Person> foundPersons = personCrudImpl.findByCity(testCity);

		assertNotNull(foundPersons);
		assertEquals(1, foundPersons.size());
		assertEquals(testPerson, foundPersons.get(0));
	}

	@Test
	public void testDeletePerson() throws IOException {
		Person personToDelete = new Person();
		personToDelete.setFirstName("John");
		personToDelete.setLastName("Doe");

		List<Person> currentPersons = new ArrayList<>();
		currentPersons.add(personToDelete);

		when(dataStore.getPersons()).thenReturn(currentPersons);

		Mockito.doNothing().when(dataStore).saveToFile(objectMapper);

		personCrudImpl.delete(personToDelete);

		verify(dataStore, times(1)).saveToFile(objectMapper);
		assertEquals(0, currentPersons.size());
	}
}
