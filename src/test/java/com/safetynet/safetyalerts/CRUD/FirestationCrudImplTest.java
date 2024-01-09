package com.safetynet.safetyalerts.CRUD;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.CRUD.FirestationCrudImpl;
import com.safetynet.safetynetalerts.configuration.DataStore;
import com.safetynet.safetynetalerts.model.Firestation;

@ExtendWith(MockitoExtension.class)
public class FirestationCrudImplTest {

	@Mock
	private DataStore dataStore;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private FirestationCrudImpl firestationCrudImpl;

	@Test
	public void testSaveFirestation() throws IOException {
		Firestation firestationToSave = new Firestation();
		firestationToSave.setStation(1);
		firestationToSave.setAddress("123 Main St");

		List<Firestation> currentFirestations = new ArrayList<>();
		when(dataStore.getFirestations()).thenReturn(currentFirestations);

		Mockito.doNothing().when(dataStore).saveToFile(objectMapper);

		Firestation savedFirestation = firestationCrudImpl.save(firestationToSave);

		assertEquals(1, currentFirestations.size());
		assertEquals(firestationToSave, currentFirestations.get(0));

		verify(dataStore, times(1)).saveToFile(objectMapper);

		assertEquals(firestationToSave, savedFirestation);
	}

	@Test
	public void testFindByAddress() {
		List<Firestation> firestations = new ArrayList<>();
		Firestation firestation = new Firestation();
		firestation.setStation(1);
		firestation.setAddress("123 Main St");
		firestations.add(firestation);

		when(dataStore.getFirestations()).thenReturn(firestations);

		Firestation result = firestationCrudImpl.findByAddress("123 Main St");

		assertEquals(firestation, result);

		verify(dataStore, times(1)).getFirestations();
	}

	@Test
	public void testFindAllByStation() {
		List<Firestation> firestations = new ArrayList<>();
		Firestation firestation1 = new Firestation();
		firestation1.setStation(1);
		firestation1.setAddress("123 Main St");

		firestations.add(firestation1);

		when(dataStore.getFirestations()).thenReturn(firestations);

		List<Firestation> result = firestationCrudImpl.findAllByStation(1);

		assertEquals(1, result.size());
		assertEquals(firestation1, result.get(0));

		verify(dataStore, times(1)).getFirestations();
	}

	@Test
	public void testDeleteFirestation() throws IOException {
		Firestation firestationToDelete = new Firestation();
		firestationToDelete.setStation(1);
		firestationToDelete.setAddress("123 Main St");

		List<Firestation> firestations = new ArrayList<>();
		firestations.add(firestationToDelete);

		when(dataStore.getFirestations()).thenReturn(firestations);

		Mockito.doNothing().when(dataStore).saveToFile(objectMapper);
		
		firestationCrudImpl.delete(firestationToDelete);

		assertEquals(0, firestations.size());

		verify(dataStore, times(1)).saveToFile(objectMapper);
	}
}
