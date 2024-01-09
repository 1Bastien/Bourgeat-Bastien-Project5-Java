package com.safetynet.safetynetalerts.configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;

public class DataStore {

	private List<Person> persons;

	private List<Firestation> firestations;

	private List<MedicalRecord> medicalrecords;

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

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

	public List<Firestation> getFirestations() {
		return firestations;
	}

	public void setFirestations(List<Firestation> firestations) {
		this.firestations = firestations;
	}

	public List<MedicalRecord> getMedicalrecords() {
		return medicalrecords;
	}

	public void setMedicalrecords(List<MedicalRecord> medicalrecords) {
		this.medicalrecords = medicalrecords;
	}
}