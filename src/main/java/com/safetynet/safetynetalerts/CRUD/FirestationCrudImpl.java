package com.safetynet.safetynetalerts.CRUD;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.configuration.DataStore;
import com.safetynet.safetynetalerts.model.Firestation;

@Service
public class FirestationCrudImpl implements FirestationCRUD {

	private static final Logger logger = LogManager.getLogger("FirestationCrudImpl");

	private DataStore dataStore;

	private ObjectMapper objectMapper;

	public FirestationCrudImpl(DataStore dataStore, ObjectMapper objectMapper) {
		this.dataStore = dataStore;
		this.objectMapper = objectMapper;
	}

	@Override
	public Firestation save(Firestation firestation) throws IOException {
		List<Firestation> firestations = dataStore.getFirestations();

		try {
			firestations.add(firestation);
			dataStore.saveToFile(objectMapper);

			return firestation;
		} catch (Exception e) {
			logger.error("Error while saving firestation.", e);
			throw new RuntimeException("Error while saving firestation.", e);
		}
	}

	@Override
	public Firestation findByAddress(String address) {
		List<Firestation> firestations = dataStore.getFirestations();

		try {
			return firestations.stream().filter(firestation -> firestation.getAddress().equals(address)).findFirst()
					.orElse(null);
		} catch (Exception e) {
			logger.error("Error while finding firestation.", e);
			throw new RuntimeException("Error while finding firestation.", e);
		}
	}

	@Override
	public List<Firestation> findAllByStation(int station) {
		List<Firestation> firestations = dataStore.getFirestations();

		try {
			return firestations.stream().filter(firestation -> firestation.getStation() == station)
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("Error while finding firestation.", e);
			throw new RuntimeException("Error while finding firestation.", e);
		}
	}

	@Override
	public void delete(Firestation firestation) throws IOException {
		List<Firestation> firestations = dataStore.getFirestations();

		try {
			firestations.remove(firestation);
			dataStore.saveToFile(objectMapper);
		} catch (Exception e) {
			logger.error("Error while deleting firestation.", e);
			throw new RuntimeException("Error while deleting firestation.", e);
		}
	}
}
