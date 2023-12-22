package com.safetynet.safetynetalerts.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.service.FirestationService;

import jakarta.validation.Valid;

@RestController
public class FirestationController {

	@Autowired
	private FirestationService firestationService;

	@PostMapping(value = "/firestation", produces = MediaType.APPLICATION_JSON_VALUE)
	public Firestation postFirestation(@Valid @RequestBody Firestation firestation) {
		return firestationService.postFirestation(firestation);
	}

	@PutMapping(value = "/firestation/{address}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Firestation putFirestation(@PathVariable("address") String address,
			@Valid @RequestBody Firestation firestation) {
		return firestationService.putFirestation(address, firestation);
	}

	@DeleteMapping(value = "/firestation/{address}", produces = MediaType.TEXT_PLAIN_VALUE)
	public String deleteFirestation(@PathVariable("address") String address) {
		return firestationService.deleteFirestation(address);
	}

	@GetMapping(value = "/firestation", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String, Object>> getListOfPersonByFirestation(@RequestParam("stationNumber") int stationNumber) {
		return firestationService.getListOfPersonByFirestation(stationNumber);
	}

	@GetMapping(value = "/phoneAlert", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getPhoneNumbersByFirestation(@RequestParam("firestation") int firestationNumber) {
		return firestationService.getPhoneNumbersByFirestation(firestationNumber);
	}

	@GetMapping(value = "/flood", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String, Object>> getPersonsByStations(@RequestParam("stations") List<Integer> stationNumbers) {
		return firestationService.getPersonsByStations(stationNumbers);
	}

}
