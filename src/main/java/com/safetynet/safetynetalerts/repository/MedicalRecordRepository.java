package com.safetynet.safetynetalerts.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;

@Repository
public interface MedicalRecordRepository extends CrudRepository<MedicalRecord, Long> {
	
	Optional<MedicalRecord> findByPerson(Person person);
}