package com.safetynet.safetynetalerts.CRUD;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.configuration.DataStore;
import com.safetynet.safetynetalerts.model.Person;

@Service
public class PersonCrudImpl implements PersonCRUD {

	private static final Logger logger = LogManager.getLogger("PersonCrudImpl");

	private DataStore dataStore;

	private ObjectMapper objectMapper;

	public PersonCrudImpl(DataStore dataStore, ObjectMapper objectMapper) {
		this.dataStore = dataStore;
		this.objectMapper = objectMapper;
	}

	@Override
	public Person save(Person person) throws IOException {
		List<Person> persons = dataStore.getPersons();

		try {
			persons.add(person);
			dataStore.saveToFile(objectMapper);

			return person;
		} catch (Exception e) {
			logger.error("Error while saving person.", e);
			throw new RuntimeException("Error while saving person.", e);
		}
	}

	@Override
	public Person findByFirstNameAndLastName(String firstName, String lastName) throws IOException {
		List<Person> persons = dataStore.getPersons();

		try {
			return persons.stream()
					.filter(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName))
					.findFirst().orElse(null);
		} catch (Exception e) {
			logger.error("Error while finding person.", e);
			throw new RuntimeException("Error while finding person.", e);
		}
	}

	@Override
	public List<Person> findByAddress(String address) throws IOException {
		List<Person> persons = dataStore.getPersons();

		try {
			return persons.stream().filter(person -> person.getAddress().equals(address)).collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("Error while finding person.", e);
			throw new RuntimeException("Error while finding person.", e);
		}
	}

	@Override
	public List<Person> findByCity(String city) throws IOException {
		List<Person> persons = dataStore.getPersons();

		try {
			return persons.stream().filter(person -> person.getCity().equals(city)).collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("Error while finding person.", e);
			throw new RuntimeException("Error while finding person.", e);
		}
	}

	@Override
	public void delete(Person person) throws IOException {
		List<Person> persons = dataStore.getPersons();
		try {
			persons.remove(person);
			dataStore.saveToFile(objectMapper);
		} catch (Exception e) {
			logger.error("Error while deleting person.", e);
			throw new RuntimeException("Error while deleting person.", e);
		}
	}
}
