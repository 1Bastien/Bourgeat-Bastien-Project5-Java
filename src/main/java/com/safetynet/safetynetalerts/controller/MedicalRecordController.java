package com.safetynet.safetynetalerts.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

	@PostMapping(value = "/medicalRecord", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MedicalRecord> postMedicalRecord(@Valid @RequestBody MedicalRecord medicalRecord)
			throws IOException {
		return new ResponseEntity<>(medicalRecordService.postMedicalRecord(medicalRecord), HttpStatus.CREATED);
	}

	@PutMapping(value = "/medicalRecord/{firstName}/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MedicalRecord> putMedicalRecord(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName, @Valid @RequestBody MedicalRecord medicalRecord)
			throws IOException {
		return new ResponseEntity<>(medicalRecordService.putMedicalRecord(firstName, lastName, medicalRecord),
				HttpStatus.OK);
	}

	@DeleteMapping(value = "/medicalRecord/{firstName}/{lastName}", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> deleteMedicalRecord(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName) throws IOException {
		return new ResponseEntity<>(medicalRecordService.deleteMedicalRecord(firstName, lastName), HttpStatus.OK);
	}
}
