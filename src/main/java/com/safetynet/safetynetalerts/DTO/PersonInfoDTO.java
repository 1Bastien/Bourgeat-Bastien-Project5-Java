package com.safetynet.safetynetalerts.DTO;

import java.util.List;

public class PersonInfoDTO {

	private String firstName;

	private String lastName;

	private String address;

	private String email;

	private int age;

	private List<String> medications;

	private List<String> allergies;

	private String phone;

	private int firestationNumber;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getFirestationNumber() {
		return firestationNumber;
	}

	public void setFirestationNumber(int firestationNumber) {
		this.firestationNumber = firestationNumber;
	}
}
