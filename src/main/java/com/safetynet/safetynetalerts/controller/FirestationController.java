package com.safetynet.safetynetalerts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.service.FirestationService;

import jakarta.validation.Valid;

@RestController
public class FirestationController {

	@Autowired
	private FirestationService firestationService;

	@PostMapping("/firestation")
	public Firestation postFirestation(@Valid @RequestBody Firestation firestation) {
		return firestationService.postFirestation(firestation);
	}

	@PutMapping(value = "/firestation/{address}", produces = MediaType.TEXT_PLAIN_VALUE)
	public Firestation putFirestation(@PathVariable("address") String address,
			@Valid @RequestBody Firestation firestation) {
		return firestationService.putFirestation(address, firestation);
	}

	@DeleteMapping(value = "/firestation/{address}", produces = MediaType.TEXT_PLAIN_VALUE)
	public String deleteFirestation(@PathVariable("address") String address) {
		return firestationService.deleteFirestation(address);
	}
}
