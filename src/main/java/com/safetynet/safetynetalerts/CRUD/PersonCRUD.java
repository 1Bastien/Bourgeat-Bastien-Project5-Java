package com.safetynet.safetynetalerts.CRUD;

import java.io.IOException;
import java.util.List;

import com.safetynet.safetynetalerts.model.Person;

public interface PersonCRUD {

	Person save(Person person) throws IOException;

	Person findByFirstNameAndLastName(String firstName, String lastName) throws IOException;

	List<Person> findByAddress(String address) throws IOException;

	List<Person> findByCity(String city) throws IOException;

	void delete(Person person) throws IOException;
}
