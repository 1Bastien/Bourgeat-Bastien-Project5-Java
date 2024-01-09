package com.safetynet.safetynetalerts.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.safetynetalerts.DTO.FirestationInfoDTO;
import com.safetynet.safetynetalerts.DTO.PersonsByStationsDTO;
import com.safetynet.safetynetalerts.DTO.PhoneNumbersDTO;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.service.FirestationService;

import jakarta.validation.Valid;

@RestController
public class FirestationController {

	@Autowired
	private FirestationService firestationService;

	@PostMapping(value = "/firestation", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Firestation> postFirestation(@Valid @RequestBody Firestation firestation) {
		return new ResponseEntity<>(firestationService.postFirestation(firestation), HttpStatus.CREATED);
	}

	@PutMapping(value = "/firestation/{address}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Firestation> putFirestation(@PathVariable("address") String address,
			@Valid @RequestBody Firestation firestation) {
		return new ResponseEntity<>(firestationService.putFirestation(address, firestation), HttpStatus.OK);
	}

	@DeleteMapping(value = "/firestation/{address}", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> deleteFirestation(@PathVariable("address") String address) {
		return new ResponseEntity<>(firestationService.deleteFirestation(address), HttpStatus.OK);
	}

	@GetMapping(value = "/firestation", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FirestationInfoDTO> getListOfPersonByFirestation(
			@RequestParam("stationNumber") int stationNumber) {
		return new ResponseEntity<>(firestationService.getListOfPersonByFirestation(stationNumber), HttpStatus.OK);
	}

	@GetMapping(value = "/phoneAlert", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PhoneNumbersDTO> getPhoneNumbersByFirestation(
			@RequestParam("firestation") int firestationNumber) {
		return new ResponseEntity<>(firestationService.getPhoneNumbersByFirestation(firestationNumber), HttpStatus.OK);
	}

	@GetMapping(value = "/flood/stations", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PersonsByStationsDTO> getPersonsByStations(
			@RequestParam("stations") List<Integer> stationNumbers) {
		return new ResponseEntity<>(firestationService.getPersonsByStations(stationNumbers), HttpStatus.OK);
	}

}
