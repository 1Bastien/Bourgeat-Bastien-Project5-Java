package com.safetynet.safetynetalerts.DTO;

import java.util.List;

public class PersonsByStationsDTO {

	private List<PersonInfoDTO> personsByStations;

	public List<PersonInfoDTO> getPersonsByStations() {
		return personsByStations;
	}

	public void setPersonsByStations(List<PersonInfoDTO> personsByStations) {
		this.personsByStations = personsByStations;
	}
}
