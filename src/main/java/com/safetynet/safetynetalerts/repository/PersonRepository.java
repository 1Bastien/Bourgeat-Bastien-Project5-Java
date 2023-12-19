package com.safetynet.safetynetalerts.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.safetynet.safetynetalerts.model.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

	Optional<Person> findByFirstNameAndLastName(String firstName, String lastName);
}