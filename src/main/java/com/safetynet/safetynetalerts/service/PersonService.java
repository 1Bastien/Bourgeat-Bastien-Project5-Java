package com.safetynet.safetynetalerts.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.PersonRepository;

@Service
public class PersonService {

	private static final Logger logger = LogManager.getLogger("PersonService");

	@Autowired
	private PersonRepository personRepository;

	@Transactional
	public Person postPerson(Person person) {

		if (personRepository.findByFirstNameAndLastName(person.getFirstName(), person.getLastName()).isPresent()) {
			logger.error("This person already exists: {}", person);
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Person with ID: " + person.getFirstName() + " " + person.getLastName() + " already exists");
		}

		try {
			return personRepository.save(person);
		} catch (Exception e) {
			logger.error("Unable to create a new person", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create a new person", e);
		}
	}

	@Transactional
	public Person putPerson(String firstName, String lastName, Person newPerson) {
		Optional<Person> personOptional = personRepository.findByFirstNameAndLastName(firstName, lastName);
		if (personOptional.isEmpty()) {
			logger.error("This person doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This person doesn't exist");
		}

		Person oldPerson = personOptional.get();

		try {

			oldPerson.setAddress(newPerson.getAddress());
			oldPerson.setCity(newPerson.getCity());
			oldPerson.setEmail(newPerson.getEmail());
			oldPerson.setPhone(newPerson.getPhone());
			oldPerson.setZip(newPerson.getZip());

			personRepository.save(oldPerson);

			return oldPerson;

		} catch (Exception e) {
			logger.error("Unable to update this person", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to update this person", e);
		}
	}

	@Transactional
	public String deletePerson(String firstName, String lastName) {
		Optional<Person> personOptional = personRepository.findByFirstNameAndLastName(firstName, lastName);
		if (personOptional.isEmpty()) {
			logger.error("This person doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This person doesn't exist");
		}

		Person person = personOptional.get();

		try {
			personRepository.delete(person);
			return "Person deleted";
		} catch (Exception e) {
			logger.error("Unable to delete this person", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete this person", e);
		} finally {
			logger.info("Person deleted: {}", person);
		}
	}
}
