package com.safetynet.safetynetalerts.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.repository.FirestationRepository;

@Service
public class FirestationService {

	private static final Logger logger = LogManager.getLogger("FirestationService");

	@Autowired
	private FirestationRepository firestationRepository;

	@Transactional
	public Firestation postFirestation(Firestation firestation) {
		if (firestationRepository.findByAddress(firestation.getAddress()).isPresent()) {
			logger.error("This Firestation already exist :", firestation);
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Firestation is already assigned for this address :" + firestation.getAddress());
		}

		try {
			return firestationRepository.save(firestation);

		} catch (Exception e) {
			logger.error("Unable to create a new firestation object", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to create a new firestation object", e);
		}
	}

	@Transactional
	public Firestation putFirestation(String address, Firestation newFirestation) {
		Optional<Firestation> firestationOptional = firestationRepository.findByAddress(address);
		if (firestationOptional.isEmpty()) {
			logger.error("This firestation doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This firestation doesn't exist");
		}

		Firestation oldFirestation = firestationOptional.get();

		try {

			oldFirestation.setStation(newFirestation.getStation());

			firestationRepository.save(oldFirestation);

			return oldFirestation;

		} catch (Exception e) {
			logger.error("Unable to update this firestation object", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to update this firestation object", e);
		}
	}

	@Transactional
	public String deleteFirestation(String address) {
		Optional<Firestation> firestation = firestationRepository.findByAddress(address);
		if (firestation.isEmpty()) {
			logger.error("This firestation doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This firestation doesn't exist");
		}

		try {
			firestationRepository.delete(firestation.get());
			return ("Firestation object deleted");
		} catch (Exception e) {
			logger.error("Unable to delete this firestation object", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to delete this firestation object", e);
		} finally {
			logger.info("Firestation object deleted");
		}
	}
}
