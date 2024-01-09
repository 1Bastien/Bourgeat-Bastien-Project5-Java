package com.safetynet.safetynetalerts.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.safetynet.safetynetalerts.CRUD.MedicalRecordCRUD;
import com.safetynet.safetynetalerts.CRUD.PersonCRUD;
import com.safetynet.safetynetalerts.model.MedicalRecord;

@Service
public class MedicalRecordService {

	private static final Logger logger = LogManager.getLogger("MedicalRecordService");

	private MedicalRecordCRUD medicalRecordCRUD;

	private PersonCRUD personCRUD;

	public MedicalRecordService(MedicalRecordCRUD medicalRecordCRUD, PersonCRUD personCRUD) {
		this.medicalRecordCRUD = medicalRecordCRUD;
		this.personCRUD = personCRUD;
	}

	@Transactional
	public MedicalRecord postMedicalRecord(MedicalRecord medicalRecord) throws IOException {

		if (personCRUD.findByFirstNameAndLastName(medicalRecord.getFirstName(), medicalRecord.getLastName()) == null) {
			logger.error("This person doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This person doesn't exist");
		}

		if (medicalRecordCRUD.findByFirstNameAndLastName(medicalRecord.getFirstName(),
				medicalRecord.getLastName()) != null) {
			logger.error("This medical record already exists: {}", medicalRecord);
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This Medical record already exists");
		}

		try {
			return medicalRecordCRUD.save(medicalRecord);
		} catch (Exception e) {
			logger.error("Unable to create a new medical record", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create a new medical record",
					e);
		}
	}

	@Transactional
	public MedicalRecord putMedicalRecord(String firstName, String lastName, MedicalRecord newMedicalRecord)
			throws IOException {

		MedicalRecord medicalRecord = medicalRecordCRUD.findByFirstNameAndLastName(firstName, lastName);
		if (medicalRecord == null) {
			logger.error("This medical record doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This medical record doesn't exist");
		}

		try {

			medicalRecord.setBirthdate(newMedicalRecord.getBirthdate());
			medicalRecord.setMedications(newMedicalRecord.getMedications());
			medicalRecord.setAllergies(newMedicalRecord.getAllergies());

			medicalRecordCRUD.save(medicalRecord);

			return medicalRecord;
		} catch (Exception e) {
			logger.error("Unable to update this medical record", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to update this medical record",
					e);
		}
	}

	@Transactional
	public String deleteMedicalRecord(String firstName, String lastName) throws IOException {
		MedicalRecord medicalRecord = medicalRecordCRUD.findByFirstNameAndLastName(firstName, lastName);

		if (medicalRecord == null) {
			logger.error("This medical record doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This medical record doesn't exist");
		}

		try {
			medicalRecordCRUD.delete(medicalRecord);
			return "Medical record deleted";
		} catch (Exception e) {
			logger.error("Unable to delete this medical record", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete this medical record",
					e);
		}
	}

	public int calculateAge(String birthdate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDate birthdateDate = LocalDate.parse(birthdate, formatter);

		LocalDate currentDate = LocalDate.now();
		Period period = Period.between(birthdateDate, currentDate);

		return period.getYears();
	}

}
