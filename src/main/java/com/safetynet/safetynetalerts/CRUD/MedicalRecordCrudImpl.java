package com.safetynet.safetynetalerts.CRUD;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.configuration.DataStore;
import com.safetynet.safetynetalerts.model.MedicalRecord;

@Service
public class MedicalRecordCrudImpl implements MedicalRecordCRUD {

	private DataStore dataStore;

	private ObjectMapper objectMapper;

	public MedicalRecordCrudImpl(DataStore dataStore, ObjectMapper objectMapper) {
		this.dataStore = dataStore;
		this.objectMapper = objectMapper;
	}

	@Override
	public MedicalRecord save(MedicalRecord medicalRecord) throws IOException {
		List<MedicalRecord> medicalRecords = dataStore.getMedicalrecords();

		try {
			medicalRecords.add(medicalRecord);
			dataStore.saveToFile(objectMapper);

			return medicalRecord;
		} catch (Exception e) {
			throw new RuntimeException("Error while saving medical record.", e);
		}

	}

	@Override
	public MedicalRecord findByFirstNameAndLastName(String firstName, String lastName) throws IOException {
		List<MedicalRecord> medicalRecords = dataStore.getMedicalrecords();

		try {
			return medicalRecords.stream().filter(medicalRecord -> medicalRecord.getFirstName().equals(firstName)
					&& medicalRecord.getLastName().equals(lastName)).findFirst().orElse(null);
		} catch (Exception e) {
			throw new RuntimeException("Error while finding medical record.", e);
		}
	}

	@Override
	public void delete(MedicalRecord medicalRecordToDelete) throws IOException {
		List<MedicalRecord> medicalRecords = dataStore.getMedicalrecords();

		try {
			medicalRecords
					.removeIf(medicalRecord -> medicalRecord.getFirstName().equals(medicalRecordToDelete.getFirstName())
							&& medicalRecord.getLastName().equals(medicalRecordToDelete.getLastName()));

			dataStore.saveToFile(objectMapper);
		} catch (Exception e) {
			throw new RuntimeException("Error while deleting medical record.", e);
		}
	}
}