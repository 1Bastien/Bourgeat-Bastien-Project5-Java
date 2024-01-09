package com.safetynet.safetynetalerts.configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.DTO.FirestationDTO;
import com.safetynet.safetynetalerts.DTO.MedicalRecordDTO;
import com.safetynet.safetynetalerts.DTO.PersonDTO;

public class DataStore {

	private List<PersonDTO> persons;

	private List<FirestationDTO> firestations;

	private List<MedicalRecordDTO> medicalrecords;

	public void saveToFile(ObjectMapper objectMapper) throws IOException {
		File file = new File("src/main/resources/data.json");

		try {
			objectMapper.writeValue(file, this);
			System.out.println("Data successfully saved to file.");
		} catch (IOException e) {
			System.err.println("Error saving data to file: " + e.getMessage());
			throw e;
		}
	}

	public List<PersonDTO> getPersons() {
		return persons;
	}

	public void setPersons(List<PersonDTO> persons) {
		this.persons = persons;
	}

	public List<FirestationDTO> getFirestations() {
		return firestations;
	}

	public void setFirestations(List<FirestationDTO> firestations) {
		this.firestations = firestations;
	}

	public List<MedicalRecordDTO> getMedicalrecords() {
		return medicalrecords;
	}

	public void setMedicalrecords(List<MedicalRecordDTO> medicalrecords) {
		this.medicalrecords = medicalrecords;
	}
}