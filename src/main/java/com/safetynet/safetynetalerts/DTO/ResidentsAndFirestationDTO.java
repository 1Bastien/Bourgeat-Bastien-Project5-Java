package com.safetynet.safetynetalerts.DTO;

import java.util.List;

public class ResidentsAndFirestationDTO {

	private List<ResidentDTO> residents;

	private FirestationInfoDTO firestationInfo;

	public List<ResidentDTO> getResidents() {
		return residents;
	}

	public void setResidents(List<ResidentDTO> residents) {
		this.residents = residents;
	}

	public FirestationInfoDTO getFirestationInfo() {
		return firestationInfo;
	}

	public void setFirestationInfo(FirestationInfoDTO firestationInfo) {
		this.firestationInfo = firestationInfo;
	}

	public static class ResidentDTO {

		private String firstName;

		private String lastName;

		private String phone;

		private int age;

		private List<String> medications;

		private List<String> allergies;

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

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
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
	}

	public static class FirestationInfoDTO {

		private int firestationNumber;

		public int getFirestationNumber() {
			return firestationNumber;
		}

		public void setFirestationNumber(int firestationNumber) {
			this.firestationNumber = firestationNumber;
		}
	}
}
