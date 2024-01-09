package com.safetynet.safetynetalerts.CRUD;

import java.io.IOException;

import com.safetynet.safetynetalerts.model.MedicalRecord;

public interface MedicalRecordCRUD {

	MedicalRecord save(MedicalRecord medicalRecord) throws IOException;

	MedicalRecord findByFirstNameAndLastName(String firstName, String lastName) throws IOException;

	void delete(MedicalRecord medicalRecord) throws IOException;
}