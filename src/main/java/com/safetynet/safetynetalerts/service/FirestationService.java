package com.safetynet.safetynetalerts.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.FirestationRepository;
import com.safetynet.safetynetalerts.repository.PersonRepository;

@Service
public class FirestationService {

	private static final Logger logger = LogManager.getLogger("FirestationService");

	private FirestationRepository firestationRepository;

	private PersonRepository personRepository;

	private MedicalRecordService medicalRecordService;

	public FirestationService(FirestationRepository firestationRepository, PersonRepository personRepository,
			MedicalRecordService medicalRecordService) {
		this.firestationRepository = firestationRepository;
		this.personRepository = personRepository;
		this.medicalRecordService = medicalRecordService;
	}

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

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getListOfPersonByFirestation(int stationNumber) {
		List<Firestation> firestations = firestationRepository.findAllByStation(stationNumber);

		if (firestations.isEmpty()) {
			logger.error("This firestation doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This firestation doesn't exist");
		}
		try {

			int adultCount = 0;
			int childCount = 0;

			List<Map<String, Object>> result = new ArrayList<>();

			for (Firestation firestation : firestations) {

				List<Person> residents = personRepository.findByAddress(firestation.getAddress());

				for (Person resident : residents) {
					Map<String, Object> residentMap = new HashMap<>();
					residentMap.put("firstName", resident.getFirstName());
					residentMap.put("lastName", resident.getLastName());
					residentMap.put("address", resident.getAddress());
					residentMap.put("phone", resident.getPhone());

					int age = medicalRecordService.calculateAge(resident.getMedicalRecord().getBirthdate());

					if (age <= 18) {
						childCount++;
					} else {
						adultCount++;
					}

					result.add(residentMap);
				}
			}

			Map<String, Object> countResult = new HashMap<>();
			countResult.put("adultCount", adultCount);
			countResult.put("childCount", childCount);
			result.add(countResult);

			return result;
		} catch (Exception e) {
			logger.error("Unable to get list of person by firestation", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to get list of person by firestation", e);
		}
	}

	@Transactional(readOnly = true)
	public List<String> getPhoneNumbersByFirestation(int firestationNumber) {
		List<Firestation> firestations = firestationRepository.findAllByStation(firestationNumber);

		if (firestations.isEmpty()) {
			logger.error("No firestation found for this number");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No firestation found for this number");
		}

		try {
			List<String> phoneNumbers = new ArrayList<>();

			for (Firestation firestation : firestations) {
				List<Person> residents = personRepository.findByAddress(firestation.getAddress());
				for (Person resident : residents) {
					phoneNumbers.add(resident.getPhone());
				}
			}

			return phoneNumbers;
		} catch (Exception e) {
			logger.error("Unable to get phone numbers by firestation", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to get phone numbers by firestation", e);
		}
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getPersonsByStations(List<Integer> stationNumbers) {
		try {
			List<Map<String, Object>> result = new ArrayList<>();

			for (Integer stationNumber : stationNumbers) {
				List<Firestation> firestations = firestationRepository.findAllByStation(stationNumber);

				for (Firestation firestation : firestations) {
					List<Person> persons = personRepository.findByAddress(firestation.getAddress());

					for (Person person : persons) {
						Map<String, Object> personMap = new HashMap<>();
						personMap.put("address", person.getAddress());
						personMap.put("firstName", person.getFirstName());
						personMap.put("lastName", person.getLastName());
						personMap.put("phone", person.getPhone());
						personMap.put("age",
								medicalRecordService.calculateAge(person.getMedicalRecord().getBirthdate()));

						MedicalRecord medicalRecord = person.getMedicalRecord();
						if (medicalRecord != null) {
							personMap.put("medications", medicalRecord.getMedications());
							personMap.put("allergies", medicalRecord.getAllergies());
						}

						personMap.put("firestationNumber", firestation.getStation());
						result.add(personMap);
					}
				}
			}

			return result;
		} catch (Exception e) {
			logger.error("Unable to get residents by stations", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get residents by stations",
					e);
		}
	}

}
