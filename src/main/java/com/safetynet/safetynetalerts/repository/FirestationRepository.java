package com.safetynet.safetynetalerts.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.safetynet.safetynetalerts.model.Firestation;

@Repository
public interface FirestationRepository extends CrudRepository<Firestation, Long> {

	Optional<Firestation> findByAddress(String address);
}