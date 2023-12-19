package com.safetynet.safetynetalerts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.repository.FirestationRepository;

@ExtendWith(MockitoExtension.class)
public class FirestationServiceTest {

	@InjectMocks
	private FirestationService firestationService;

	@Mock
	private static FirestationRepository firestationRepository;

	private static Firestation TEST_FIRESTATION;

	@BeforeEach
	public void setUpPerTest() {
		TEST_FIRESTATION = new Firestation();
		TEST_FIRESTATION.setAddress("1509 Culver St");
		TEST_FIRESTATION.setStation(3);
	}

	@Test
	public void testPostFirestation() {
		when(firestationRepository.findByAddress(eq("1509 Culver St"))).thenReturn(Optional.empty());
		when(firestationRepository.save(any(Firestation.class))).thenReturn(TEST_FIRESTATION);

		Firestation result = firestationService.postFirestation(TEST_FIRESTATION);

		verify(firestationRepository, times(1)).findByAddress("1509 Culver St");
		verify(firestationRepository, times(1)).save(TEST_FIRESTATION);

		assertNotNull(result);
		assertEquals("1509 Culver St", result.getAddress());
		assertEquals(3, result.getStation());
	}

	@Test
	public void testPutFirestation() {
		Firestation newFirestation = new Firestation();
		newFirestation.setStation(4);

		when(firestationRepository.findByAddress(eq("1509 Culver St"))).thenReturn(Optional.of(TEST_FIRESTATION));
		when(firestationRepository.save(any(Firestation.class))).thenReturn(TEST_FIRESTATION);

		Firestation result = firestationService.putFirestation("1509 Culver St", newFirestation);

		verify(firestationRepository, times(1)).findByAddress("1509 Culver St");
		verify(firestationRepository, times(1)).save(TEST_FIRESTATION);

		assertNotNull(result);
		assertEquals(4, result.getStation());
	}

	@Test
	public void testDeleteFirestation() {
		when(firestationRepository.findByAddress(eq("1509 Culver St"))).thenReturn(Optional.of(TEST_FIRESTATION));

		String result = firestationService.deleteFirestation("1509 Culver St");

		verify(firestationRepository, times(1)).findByAddress("1509 Culver St");
		verify(firestationRepository, times(1)).delete(TEST_FIRESTATION);

		assertNotNull(result);
		assertEquals("Firestation object deleted", result);
	}
}
