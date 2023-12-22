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

import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.service.PersonService;

import jakarta.validation.Valid;

@RestController
public class PersonController {

	@Autowired
	private PersonService personService;

	@PostMapping(value = "/person", produces = MediaType.APPLICATION_JSON_VALUE)
	public Person postPerson(@Valid @RequestBody Person person) {
		return personService.postPerson(person);
	}

	@PutMapping(value = "/person/{firstName}/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Person putPerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName,
			@Valid @RequestBody Person person) {
		return personService.putPerson(firstName, lastName, person);
	}

	@DeleteMapping(value = "/person/{firstName}/{lastName}", produces = MediaType.TEXT_PLAIN_VALUE)
	public String deletePerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName) {
		return personService.deletePerson(firstName, lastName);
	}

	@GetMapping(value = "/childAlert", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String, Object>> getChildsByAddress(@RequestParam("address") String address) {
		return personService.getChildsByAddress(address);
	}

	@GetMapping(value = "/fire", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String, Object>> getPersonsAndFirestationByAddress(@RequestParam("address") String address) {
		return personService.getPersonsAndFirestationByAddress(address);
	}

	@GetMapping(value = "/personInfo", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String, Object>> getPersonInfoByFirstNameAndLastName(@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName) {
		return personService.getPersonInfoByFirstNameAndLastName(firstName, lastName);
	}

	@GetMapping(value = "/communityEmail", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getCommunityEmails(@RequestParam("city") String city) {
		return personService.getCommunityEmails(city);
	}
}
