package com.safetynet.safetynetalerts.DTO;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class MedicalRecordDTO {

	@NotNull
	@Length(min = 3, max = 30)
	private String firstName;

	@NotNull
	@Length(min = 3, max = 30)
	private String lastName;

	@NotNull
	@Pattern(regexp = "\\d{2}/\\d{2}/\\d{4}")
	private String birthdate;

	private List<String> medications;

	private List<String> allergies;

	public MedicalRecordDTO() {
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public List<String> getMedications() {
		return medications;
	}

	public void setMedications(List<String> medications) {
		this.medications = medications;
	}

	public List<String> getAllergies() {
		return allergies;
	}

	public void setAllergies(List<String> allergies) {
		this.allergies = allergies;
	}
}
