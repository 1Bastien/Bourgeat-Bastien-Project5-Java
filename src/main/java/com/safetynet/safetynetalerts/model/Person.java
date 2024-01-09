package com.safetynet.safetynetalerts.model;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "firstName", "lastName" }) })
public class Person {

	@Id
	@NotNull
	@Length(min = 3, max = 30)
	private String firstName;

	@Id
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