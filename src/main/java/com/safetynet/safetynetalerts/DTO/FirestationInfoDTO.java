package com.safetynet.safetynetalerts.DTO;

import java.util.List;

public class FirestationInfoDTO {

	private List<ResidentDTO> residents;

	private int adultCount;

	private int childCount;

	public List<ResidentDTO> getResidents() {
		return residents;
	}

	public void setResidents(List<ResidentDTO> residents) {
		this.residents = residents;
	}

	public int getAdultCount() {
		return adultCount;
	}

	public void setAdultCount(int adultCount) {
		this.adultCount = adultCount;
	}

	public int getChildCount() {
		return childCount;
	}

	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}

	public static class ResidentDTO {

		private String firstName;

		private String lastName;

		private String address;

		private String phone;

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

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}
	}
}
