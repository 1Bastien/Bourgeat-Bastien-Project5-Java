package com.safetynet.safetynetalerts.controller;

import java.io.IOException;

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

import com.safetynet.safetynetalerts.DTO.CommunityEmailsDTO;
import com.safetynet.safetynetalerts.DTO.ListChildDTO;
import com.safetynet.safetynetalerts.DTO.PersonInfoDTO;
import com.safetynet.safetynetalerts.DTO.ResidentsAndFirestationDTO;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.service.PersonService;

import jakarta.validation.Valid;

@RestController
public class PersonController {

	@Autowired
	private PersonService personService;

	@PostMapping(value = "/person", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> postPerson(@Valid @RequestBody Person person) throws IOException {
		return new ResponseEntity<>(personService.postPerson(person), HttpStatus.CREATED);
	}

	@PutMapping(value = "/person/{firstName}/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> putPerson(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName, @Valid @RequestBody Person person) throws IOException {
		return new ResponseEntity<>(personService.putPerson(firstName, lastName, person), HttpStatus.OK);
	}

	@DeleteMapping(value = "/person/{firstName}/{lastName}", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> deletePerson(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName) throws IOException {
		return new ResponseEntity<>(personService.deletePerson(firstName, lastName), HttpStatus.OK);
	}

	@GetMapping(value = "/childAlert", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ListChildDTO> getChildsByAddress(@RequestParam("address") String address) {
		ListChildDTO result = personService.getChildsByAddress(address);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping(value = "/fire", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResidentsAndFirestationDTO> getPersonsAndFirestationByAddress(
			@RequestParam("address") String address) throws IOException {
		return new ResponseEntity<>(personService.getResidentsAndFirestationByAddress(address), HttpStatus.OK);
	}

	@GetMapping(value = "/personInfo", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PersonInfoDTO> getPersonInfoByFirstNameAndLastName(
			@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) throws IOException {
		return new ResponseEntity<>(personService.getPersonInfoByFirstNameAndLastName(firstName, lastName),
				HttpStatus.OK);
	}

	@GetMapping(value = "/communityEmail", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommunityEmailsDTO> getCommunityEmails(@RequestParam("city") String city) throws IOException {
		return new ResponseEntity<>(personService.getCommunityEmails(city), HttpStatus.OK);
	}
}
