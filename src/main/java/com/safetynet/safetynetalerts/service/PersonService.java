package com.safetynet.safetynetalerts.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.safetynet.safetynetalerts.CRUD.FirestationCRUD;
import com.safetynet.safetynetalerts.CRUD.MedicalRecordCRUD;
import com.safetynet.safetynetalerts.CRUD.PersonCRUD;
import com.safetynet.safetynetalerts.DTO.CommunityEmailsDTO;
import com.safetynet.safetynetalerts.DTO.ListChildDTO;
import com.safetynet.safetynetalerts.DTO.PersonInfoDTO;
import com.safetynet.safetynetalerts.DTO.ResidentsAndFirestationDTO;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;

@Service
public class PersonService {

	private static final Logger logger = LogManager.getLogger("PersonService");

	private PersonCRUD personCRUD;

	private MedicalRecordService medicalRecordService;

	private MedicalRecordCRUD medicalRecordCRUD;

	private FirestationCRUD firestationCRUD;

	public PersonService(PersonCRUD personCRUD, MedicalRecordService medicalRecordService,
			MedicalRecordCRUD medicalRecordCRUD, FirestationCRUD firestationCRUD) {
		this.personCRUD = personCRUD;
		this.medicalRecordService = medicalRecordService;
		this.medicalRecordCRUD = medicalRecordCRUD;
		this.firestationCRUD = firestationCRUD;
	}

	@Transactional
	public Person postPerson(Person person) throws IOException {

		if (personCRUD.findByFirstNameAndLastName(person.getFirstName(), person.getLastName()) != null) {
			logger.error("This person already exists: {}", person);
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This person already exists");
		}

		try {

			return personCRUD.save(person);
		} catch (Exception e) {
			logger.error("Unable to create a new person", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create a new person", e);
		}
	}

	@Transactional
	public Person putPerson(String firstName, String lastName, Person newPerson) throws IOException {
		Person oldPerson = personCRUD.findByFirstNameAndLastName(firstName, lastName);
		if (oldPerson == null) {
			logger.error("This person doesn't exists: {}", newPerson);
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This person doesn't exists");
		}

		try {

			oldPerson.setAddress(newPerson.getAddress());
			oldPerson.setCity(newPerson.getCity());
			oldPerson.setEmail(newPerson.getEmail());
			oldPerson.setPhone(newPerson.getPhone());
			oldPerson.setZip(newPerson.getZip());

			return personCRUD.save(oldPerson);

		} catch (Exception e) {
			logger.error("Unable to update this person", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to update this person", e);
		}
	}

	@Transactional
	public String deletePerson(String firstName, String lastName) throws IOException {
		Person person = personCRUD.findByFirstNameAndLastName(firstName, lastName);
		if (person == null) {
			logger.error("This person doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This person doesn't exist");
		}

		try {
			personCRUD.delete(person);
			logger.info("Person deleted: {}", person);
			return "Person deleted";
		} catch (Exception e) {
			logger.error("Unable to delete this person", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete this person", e);
		}
	}

	@Transactional(readOnly = true)
	public ListChildDTO getChildsByAddress(String address) {
		try {
			List<Person> persons = personCRUD.findByAddress(address);

			ListChildDTO result = new ListChildDTO();
			result.setChildren(new ArrayList<>());

			for (Person person : persons) {
				String birthDate = medicalRecordCRUD
						.findByFirstNameAndLastName(person.getFirstName(), person.getLastName()).getBirthdate();
				int age = medicalRecordService.calculateAge(birthDate);

				if (age <= 18) {
					ListChildDTO.ChildDTO childDTO = new ListChildDTO.ChildDTO();
					childDTO.setFirstName(person.getFirstName());
					childDTO.setLastName(person.getLastName());
					childDTO.setAge(age);
					childDTO.setOtherMembers(new ArrayList<>());

					for (Person otherPerson : persons) {
						if (!otherPerson.equals(person)) {
							ListChildDTO.OtherPersonDTO otherPersonDTO = new ListChildDTO.OtherPersonDTO();
							otherPersonDTO.setFirstName(otherPerson.getFirstName());
							otherPersonDTO.setLastName(otherPerson.getLastName());
							otherPersonDTO.setAge(medicalRecordService.calculateAge(medicalRecordCRUD
									.findByFirstNameAndLastName(otherPerson.getFirstName(), otherPerson.getLastName())
									.getBirthdate()));
							childDTO.getOtherMembers().add(otherPersonDTO);
						}
					}

					result.getChildren().add(childDTO);
				}
			}

			return result;
		} catch (Exception e) {
			logger.error("Unable to get persons by address", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get persons by address", e);
		}
	}

	@Transactional(readOnly = true)
	public ResidentsAndFirestationDTO getResidentsAndFirestationByAddress(String address) throws IOException {
		List<Person> residents = personCRUD.findByAddress(address);

		if (residents.isEmpty()) {
			logger.error("No residents found for this address");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No residents found for this address");
		}

		Firestation firestation = firestationCRUD.findByAddress(address);
		if (firestation == null) {
			logger.error("No firestation found for this address");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No firestation found for this address");
		}

		try {

			ResidentsAndFirestationDTO result = new ResidentsAndFirestationDTO();
			result.setResidents(new ArrayList<>());

			for (Person resident : residents) {
				ResidentsAndFirestationDTO.ResidentDTO residentDTO = new ResidentsAndFirestationDTO.ResidentDTO();
				residentDTO.setFirstName(resident.getFirstName());
				residentDTO.setLastName(resident.getLastName());
				residentDTO.setPhone(resident.getPhone());

				MedicalRecord medicalRecord = medicalRecordCRUD.findByFirstNameAndLastName(resident.getFirstName(),
						resident.getLastName());
				if (medicalRecord != null) {
					residentDTO.setAge(medicalRecordService.calculateAge(medicalRecord.getBirthdate()));
					residentDTO.setMedications(medicalRecord.getMedications());
					residentDTO.setAllergies(medicalRecord.getAllergies());
				}

				result.getResidents().add(residentDTO);
			}

			int firestationNumber = firestation.getStation();

			ResidentsAndFirestationDTO.FirestationInfoDTO firestationInfo = new ResidentsAndFirestationDTO.FirestationInfoDTO();
			firestationInfo.setFirestationNumber(firestationNumber);
			result.setFirestationInfo(firestationInfo);

			return result;
		} catch (Exception e) {
			logger.error("Unable to get residents and firestation by address", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to get residents and firestation by address", e);
		}
	}

	@Transactional(readOnly = true)
	public PersonInfoDTO getPersonInfoByFirstNameAndLastName(String firstName, String lastName) throws IOException {
		Person person = personCRUD.findByFirstNameAndLastName(firstName, lastName);
		if (person == null) {
			logger.error("This person doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This person doesn't exist");
		}

		try {
			PersonInfoDTO personInfoDTO = new PersonInfoDTO();
			personInfoDTO.setFirstName(person.getFirstName());
			personInfoDTO.setLastName(person.getLastName());
			personInfoDTO.setAddress(person.getAddress());
			personInfoDTO.setEmail(person.getEmail());
			personInfoDTO.setAge(medicalRecordService.calculateAge(medicalRecordCRUD
					.findByFirstNameAndLastName(person.getFirstName(), person.getLastName()).getBirthdate()));

			MedicalRecord medicalRecordOfPerson = medicalRecordCRUD.findByFirstNameAndLastName(person.getFirstName(),
					person.getLastName());
			if (medicalRecordOfPerson != null) {
				personInfoDTO.setMedications(medicalRecordOfPerson.getMedications());
				personInfoDTO.setAllergies(medicalRecordOfPerson.getAllergies());
			}

			return personInfoDTO;
		} catch (Exception e) {
			logger.error("Unable to get person info", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get person info", e);
		}
	}

	@Transactional(readOnly = true)
	public CommunityEmailsDTO getCommunityEmails(String city) throws IOException {
		List<Person> residents = personCRUD.findByCity(city);

		if (residents.isEmpty()) {
			logger.error("No residents found for the city: {}", city);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No residents found for the city");
		}
		try {
			List<String> emails = residents.stream().map(Person::getEmail).collect(Collectors.toList());
			CommunityEmailsDTO communityEmailsDTO = new CommunityEmailsDTO();
			communityEmailsDTO.setEmails(emails);
			return communityEmailsDTO;
		} catch (Exception e) {
			logger.error("Unable to get community emails", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get community emails", e);
		}
	}

}
