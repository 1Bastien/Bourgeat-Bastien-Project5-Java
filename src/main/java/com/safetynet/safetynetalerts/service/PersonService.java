package com.safetynet.safetynetalerts.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.FirestationRepository;
import com.safetynet.safetynetalerts.repository.PersonRepository;

@Service
public class PersonService {

	private static final Logger logger = LogManager.getLogger("PersonService");

	private PersonRepository personRepository;

	private MedicalRecordService medicalRecordService;

	private FirestationRepository firestationRepository;

	public PersonService(PersonRepository personRepository, MedicalRecordService medicalRecordService,
			FirestationRepository firestationRepository) {
		this.personRepository = personRepository;
		this.medicalRecordService = medicalRecordService;
		this.firestationRepository = firestationRepository;
	}

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

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getChildsByAddress(String address) {
		try {
			List<Person> persons = personRepository.findByAddress(address);

			if (persons.isEmpty()) {
				logger.error("no person exists for this address");
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no person exists for this address");
			}

			List<Map<String, Object>> result = new ArrayList<>();

			for (Person child : persons) {
				String birthDate = child.getMedicalRecord().getBirthdate();
				int age = medicalRecordService.calculateAge(birthDate);

				if (age <= 18) {
					Map<String, Object> childMap = new HashMap<>();
					childMap.put("firstName", child.getFirstName());
					childMap.put("lastName", child.getLastName());
					childMap.put("age", age);

					List<Map<String, Object>> otherMembers = new ArrayList<>();

					for (Person otherPerson : persons) {
						if (!otherPerson.equals(child)) {
							Map<String, Object> otherPersonMap = new HashMap<>();
							otherPersonMap.put("firstName", otherPerson.getFirstName());
							otherPersonMap.put("lastName", otherPerson.getLastName());
							otherMembers.add(otherPersonMap);
						}
					}

					childMap.put("otherMembers", otherMembers);

					result.add(childMap);
				}
			}
			return result;
		} catch (Exception e) {
			logger.error("Unable to get childs by address", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get childs by address", e);
		}
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getPersonsAndFirestationByAddress(String address) {
		try {
			List<Person> residents = personRepository.findByAddress(address);

			if (residents.isEmpty()) {
				logger.error("No residents found for this address");
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No residents found for this address");
			}

			Optional<Firestation> firestation = firestationRepository.findByAddress(address);
			if (firestation.isEmpty()) {
				logger.error("No firestation found for this address");
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No firestation found for this address");
			}

			int firestationNumber = firestation.get().getStation();

			List<Map<String, Object>> result = new ArrayList<>();

			for (Person resident : residents) {
				Map<String, Object> residentMap = new HashMap<>();
				residentMap.put("firstName", resident.getFirstName());
				residentMap.put("lastName", resident.getLastName());
				residentMap.put("phone", resident.getPhone());
				residentMap.put("age", medicalRecordService.calculateAge(resident.getMedicalRecord().getBirthdate()));

				MedicalRecord medicalRecord = resident.getMedicalRecord();
				if (medicalRecord != null) {
					residentMap.put("medications", medicalRecord.getMedications());
					residentMap.put("allergies", medicalRecord.getAllergies());
				}

				result.add(residentMap);
			}

			Map<String, Object> firestationInfo = new HashMap<>();
			firestationInfo.put("firestationNumber", firestationNumber);
			result.add(firestationInfo);

			return result;
		} catch (Exception e) {
			logger.error("Unable to get residents and firestation by address", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to get residents and firestation by address", e);
		}
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getPersonInfoByFirstNameAndLastName(String firstName, String lastName) {
		try {
			Optional<Person> matchingPersons = personRepository.findByFirstNameAndLastName(firstName, lastName);

			if (matchingPersons.isEmpty()) {
				logger.error("No person found with the given name: {} {}", firstName, lastName);
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No person found with the given name");
			}

			Person person = matchingPersons.get();
			List<Map<String, Object>> result = new ArrayList<>();

			Map<String, Object> personMap = new HashMap<>();
			personMap.put("firstName", person.getFirstName());
			personMap.put("lastName", person.getLastName());
			personMap.put("address", person.getAddress());
			personMap.put("email", person.getEmail());
			personMap.put("age", medicalRecordService.calculateAge(person.getMedicalRecord().getBirthdate()));

			MedicalRecord medicalRecord = person.getMedicalRecord();
			if (medicalRecord != null) {
				personMap.put("medications", medicalRecord.getMedications());
				personMap.put("allergies", medicalRecord.getAllergies());
			}

			result.add(personMap);

			return result;
		} catch (Exception e) {
			logger.error("Unable to get person info", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get person info", e);
		}
	}

	@Transactional(readOnly = true)
	public List<String> getCommunityEmails(String city) {
		try {
			List<Person> residents = personRepository.findByCity(city);

			if (residents.isEmpty()) {
				logger.error("No residents found for the city: {}", city);
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No residents found for the city");
			}

			return residents.stream().map(Person::getEmail).collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("Unable to get community emails", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get community emails", e);
		}
	}
}
