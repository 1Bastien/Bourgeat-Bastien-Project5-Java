package com.safetynet.safetynetalerts.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.MedicalRecordRepository;
import com.safetynet.safetynetalerts.repository.PersonRepository;

@Service
public class MedicalRecordService {

	private static final Logger logger = LogManager.getLogger("MedicalRecordService");

	private MedicalRecordRepository medicalRecordRepository;

	private PersonRepository personRepository;

	public MedicalRecordService(MedicalRecordRepository medicalRecordRepository, PersonRepository personRepository) {
		this.medicalRecordRepository = medicalRecordRepository;
		this.personRepository = personRepository;
	}

	@Transactional
	public MedicalRecord postMedicalRecord(String firstName, String lastName, MedicalRecord medicalRecord) {
		Optional<Person> optionalPerson = personRepository.findByFirstNameAndLastName(firstName, lastName);

		if (!optionalPerson.isPresent()) {
			logger.error("This Person doesn't exists: ", medicalRecord.getPerson());
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This Person doesn't exists: "
					+ medicalRecord.getPerson().getFirstName() + " " + medicalRecord.getPerson().getLastName());
		}

		if (medicalRecordRepository.findByPerson(optionalPerson.get()).isPresent()) {
			logger.error("This medical record already exists: {}", medicalRecord);
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Medical record with ID: " + medicalRecord.getPerson().getFirstName() + " "
							+ medicalRecord.getPerson().getLastName() + " already exists");
		}

		try {
			Person person = optionalPerson.get();
			medicalRecord.setPerson(person);

			return medicalRecordRepository.save(medicalRecord);
		} catch (Exception e) {
			logger.error("Unable to create a new medical record", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create a new medical record",
					e);
		}
	}

	@Transactional
	public MedicalRecord putMedicalRecord(String firstName, String lastName, MedicalRecord newMedicalRecord) {
		Optional<Person> personOptional = personRepository.findByFirstNameAndLastName(firstName, lastName);
		if (personOptional.isEmpty()) {
			logger.error("This person doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This person doesn't exist");
		}

		Person person = personOptional.get();

		Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findByPerson(person);
		if (medicalRecordOptional.isEmpty()) {
			logger.error("This medical record doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This medical record doesn't exist");
		}

		MedicalRecord medicalRecord = medicalRecordOptional.get();

		try {

			medicalRecord.setBirthdate(newMedicalRecord.getBirthdate());
			medicalRecord.setMedications(newMedicalRecord.getMedications());
			medicalRecord.setAllergies(newMedicalRecord.getAllergies());

			medicalRecordRepository.save(medicalRecord);

			return medicalRecord;
		} catch (Exception e) {
			logger.error("Unable to update this medical record", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to update this medical record",
					e);
		}
	}

	@Transactional
	public String deleteMedicalRecord(String firstName, String lastName) {
		Optional<Person> personOptional = personRepository.findByFirstNameAndLastName(firstName, lastName);
		if (personOptional.isEmpty()) {
			logger.error("This person doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This person doesn't exist");
		}

		Person person = personOptional.get();

		Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findByPerson(person);
		if (medicalRecordOptional.isEmpty()) {
			logger.error("This medical record doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This medical record doesn't exist");
		}

		MedicalRecord medicalRecord = medicalRecordOptional.get();

		try {
			medicalRecordRepository.delete(medicalRecord);
			return "Medical record deleted";
		} catch (Exception e) {
			logger.error("Unable to delete this medical record", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete this medical record",
					e);
		}
	}

}
