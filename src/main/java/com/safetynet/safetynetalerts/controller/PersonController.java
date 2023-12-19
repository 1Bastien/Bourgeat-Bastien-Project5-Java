package com.safetynet.safetynetalerts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.service.PersonService;

import jakarta.validation.Valid;

@RestController
public class PersonController {
	
	@Autowired
	private PersonService personService;
	
	@PostMapping("/person")
	public Person postPerson(@Valid @RequestBody Person person) {
		return personService.postPerson(person);
	}
	
	@PutMapping("/person/{firstName}/{lastName}")
	public Person putPerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName, @Valid @RequestBody Person person) {
		return personService.putPerson(firstName, lastName ,person);
	}
	
	@DeleteMapping("/person/{firstName}/{lastName}")
	public String deletePerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName) {
		return personService.deletePerson(firstName, lastName);
	}
}
