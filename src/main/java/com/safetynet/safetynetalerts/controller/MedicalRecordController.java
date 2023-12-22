package com.safetynet.safetynetalerts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.service.MedicalRecordService;

import jakarta.validation.Valid;

@RestController
public class MedicalRecordController {

	@Autowired
	private MedicalRecordService medicalRecordService;

	@PostMapping(value = "/medicalRecord/{firstName}/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public MedicalRecord postMedicalRecord(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName, @Valid @RequestBody MedicalRecord medicalRecord) {
		return medicalRecordService.postMedicalRecord(firstName, lastName, medicalRecord);
	}

	@PutMapping(value = "/medicalRecord/{firstName}/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public MedicalRecord putMedicalRecord(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName, @Valid @RequestBody MedicalRecord medicalRecord) {
		return medicalRecordService.putMedicalRecord(firstName, lastName, medicalRecord);
	}

	@DeleteMapping(value = "/medicalRecord/{firstName}/{lastName}", produces = MediaType.TEXT_PLAIN_VALUE)
	public String deleteMedicalRecord(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName) {
		return medicalRecordService.deleteMedicalRecord(firstName, lastName);
	}
}
