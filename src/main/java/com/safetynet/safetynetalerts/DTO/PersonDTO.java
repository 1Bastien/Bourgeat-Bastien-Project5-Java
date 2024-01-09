package com.safetynet.safetynetalerts.DTO;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class PersonDTO {

	@NotNull
	@Length(min = 3, max = 30)
	private String firstName;

	@NotNull
	@Length(min = 3, max = 30)
	private String lastName;

	@NotNull
	@Length(min = 3, max = 50)
	private String address;

	@NotNull
	@Length(min = 3, max = 50)
	private String city;

	@NotNull
	@Pattern(regexp = "\\d{5}")
	private String zip;

	@NotNull
	@Pattern(regexp = "\\d{3}-\\d{3}-\\d{4}")
	private String phone;

	@NotNull
	@Email
	private String email;

	public PersonDTO() {
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}