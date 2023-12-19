package com.safetynet.safetynetalerts.model;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "firstName", "lastName" }) })
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@NotNull
	@Length(min = 3, max = 30)
	@Column(nullable = false)
	private String firstName;

	@NotNull
	@Length(min = 3, max = 30)
	@Column(nullable = false)
	private String lastName;

	@NotNull
	@Length(min = 3, max = 50)
	@Column(nullable = false)
	private String address;

	@NotNull
	@Length(min = 3, max = 50)
	@Column(nullable = false)
	private String city;

	@NotNull
	@Pattern(regexp = "\\d{5}")
	@Column(nullable = false)
	private String zip;

	@NotNull
	@Pattern(regexp = "\\d{3}-\\d{3}-\\d{4}")
	@Column(nullable = false)
	private String phone;

	@NotNull
	@Email
	@Column(nullable = false)
	private String email;

	@OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
	private MedicalRecord medicalRecord;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public MedicalRecord getMedicalRecord() {
		return medicalRecord;
	}

	public void setMedicalRecord(MedicalRecord medicalRecord) {
		this.medicalRecord = medicalRecord;
	}

}
