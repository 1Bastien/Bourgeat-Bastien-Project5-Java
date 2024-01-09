package com.safetynet.safetynetalerts.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.safetynet.safetynetalerts.CRUD.FirestationCRUD;
import com.safetynet.safetynetalerts.CRUD.MedicalRecordCRUD;
import com.safetynet.safetynetalerts.CRUD.PersonCRUD;
import com.safetynet.safetynetalerts.DTO.FirestationInfoDTO;
import com.safetynet.safetynetalerts.DTO.PersonInfoDTO;
import com.safetynet.safetynetalerts.DTO.PersonsByStationsDTO;
import com.safetynet.safetynetalerts.DTO.PhoneNumbersDTO;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.model.Person;

@Service
public class FirestationService {

	private static final Logger logger = LogManager.getLogger("FirestationService");

	private FirestationCRUD firestationCRUD;

	private PersonCRUD personCRUD;

	private MedicalRecordCRUD medicalRecordCRUD;

	private MedicalRecordService medicalRecordService;

	public FirestationService(FirestationCRUD firestationCRUD, PersonCRUD personCRUD,
			MedicalRecordCRUD medicalRecordCRUD, MedicalRecordService medicalRecordService) {
		this.firestationCRUD = firestationCRUD;
		this.personCRUD = personCRUD;
		this.medicalRecordCRUD = medicalRecordCRUD;
		this.medicalRecordService = medicalRecordService;
	}

	@Transactional
	public Firestation postFirestation(Firestation firestation) {
		if (firestationCRUD.findByAddress(firestation.getAddress()) != null) {
			logger.error("This Firestation already exist :", firestation);
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Firestation is already assigned for this address :" + firestation.getAddress());
		}

		try {
			return firestationCRUD.save(firestation);

		} catch (Exception e) {
			logger.error("Unable to create a new firestation object", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to create a new firestation object", e);
		}
	}

	@Transactional
	public Firestation putFirestation(String address, Firestation newFirestation) {
		Firestation oldFirestation = firestationCRUD.findByAddress(address);
		if (oldFirestation == null) {
			logger.error("This firestation doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This firestation doesn't exist");
		}

		try {

			oldFirestation.setStation(newFirestation.getStation());

			firestationCRUD.save(oldFirestation);

			return oldFirestation;

		} catch (Exception e) {
			logger.error("Unable to update this firestation object", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to update this firestation object", e);
		}
	}

	@Transactional
	public String deleteFirestation(String address) {
		Firestation firestation = firestationCRUD.findByAddress(address);
		if (firestation == null) {
			logger.error("This firestation doesn't exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This firestation doesn't exist");
		}

		try {
			firestationCRUD.delete(firestation);

			logger.info("Firestation object deleted");
			return ("Firestation object deleted");
		} catch (Exception e) {
			logger.error("Unable to delete this firestation object", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to delete this firestation object", e);
		}
	}

	@Transactional(readOnly = true)
	public FirestationInfoDTO getListOfPersonByFirestation(int stationNumber) {
		List<Firestation> firestations = firestationCRUD.findAllByStation(stationNumber);

		if (firestations.isEmpty()) {
			logger.error("No firestation found for this number");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No firestation found for this number");
		}

		try {
			FirestationInfoDTO firestationInfoDTO = new FirestationInfoDTO();
			firestationInfoDTO.setResidents(new ArrayList<>());
			int adultCount = 0;
			int childCount = 0;

			for (Firestation firestation : firestations) {
				List<Person> residents = personCRUD.findByAddress(firestation.getAddress());

				for (Person resident : residents) {
					FirestationInfoDTO.ResidentDTO residentDTO = new FirestationInfoDTO.ResidentDTO();
					residentDTO.setFirstName(resident.getFirstName());
					residentDTO.setLastName(resident.getLastName());
					residentDTO.setAddress(resident.getAddress());
					residentDTO.setPhone(resident.getPhone());

					int age = medicalRecordService.calculateAge(medicalRecordCRUD
							.findByFirstNameAndLastName(resident.getFirstName(), resident.getLastName())
							.getBirthdate());

					if (age <= 18) {
						childCount++;
					} else {
						adultCount++;
					}

					firestationInfoDTO.getResidents().add(residentDTO);
				}
			}

			firestationInfoDTO.setAdultCount(adultCount);
			firestationInfoDTO.setChildCount(childCount);

			return firestationInfoDTO;
		} catch (Exception e) {
			logger.error("Unable to get list of person by firestation", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to get list of person by firestation", e);
		}
	}

	@Transactional(readOnly = true)
	public PhoneNumbersDTO getPhoneNumbersByFirestation(int firestationNumber) {
		List<Firestation> firestations = firestationCRUD.findAllByStation(firestationNumber);

		if (firestations.isEmpty()) {
			logger.error("No firestation found for this number");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No firestation found for this number");
		}

		try {
			PhoneNumbersDTO phoneNumbersDTO = new PhoneNumbersDTO();

			List<String> phoneNumbers = new ArrayList<>();

			for (Firestation firestation : firestations) {
				List<Person> residents = personCRUD.findByAddress(firestation.getAddress());
				for (Person resident : residents) {
					phoneNumbers.add(resident.getPhone());
				}
			}

			phoneNumbersDTO.setPhoneNumbers(phoneNumbers);

			return phoneNumbersDTO;
		} catch (Exception e) {
			logger.error("Unable to get phone numbers by firestation", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to get phone numbers by firestation", e);
		}
	}

	@Transactional(readOnly = true)
	public PersonsByStationsDTO getPersonsByStations(List<Integer> stationNumbers) {
		try {
			PersonsByStationsDTO personsByStationsDTO = new PersonsByStationsDTO();
			personsByStationsDTO.setPersonsByStations(new ArrayList<>());

			for (Integer stationNumber : stationNumbers) {
				List<Firestation> firestations = firestationCRUD.findAllByStation(stationNumber);

				for (Firestation firestation : firestations) {
					List<Person> persons = personCRUD.findByAddress(firestation.getAddress());

					for (Person person : persons) {
						PersonInfoDTO personInfoDTO = new PersonInfoDTO();
						personInfoDTO.setAddress(person.getAddress());
						personInfoDTO.setFirstName(person.getFirstName());
						personInfoDTO.setLastName(person.getLastName());
						personInfoDTO.setPhone(person.getPhone());
						personInfoDTO.setAge(medicalRecordService.calculateAge(medicalRecordCRUD
								.findByFirstNameAndLastName(person.getFirstName(), person.getLastName())
								.getBirthdate()));

						MedicalRecord medicalRecord = medicalRecordCRUD
								.findByFirstNameAndLastName(person.getFirstName(), person.getLastName());

						if (medicalRecord != null) {
							personInfoDTO.setMedications(medicalRecord.getMedications());
							personInfoDTO.setAllergies(medicalRecord.getAllergies());
						}

						personInfoDTO.setFirestationNumber(firestation.getStation());
						personsByStationsDTO.getPersonsByStations().add(personInfoDTO);
					}
				}
			}

			return personsByStationsDTO;
		} catch (Exception e) {
			logger.error("Unable to get residents by stations", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get residents by stations",
					e);
		}
	}

}
