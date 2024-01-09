package com.safetynet.safetynetalerts.DTO;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class FirestationDTO {

	@NotNull
	@Length(min = 3, max = 50)
	private String address;

	@NotNull
	@Positive
	private int station;

	public FirestationDTO() {
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getStation() {
		return station;
	}

	public void setStation(int station) {
		this.station = station;
	}
}
