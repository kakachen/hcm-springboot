package br.com.litecode.domain;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "patient")
@Cacheable
public class Patient {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "patient_id")
	private Integer patientId;

	@Column
	private String name;

	@Column(name = "cpf")
	private String cpf;

	@Embedded
	private Address address;

	@Column
	@Pattern(regexp = "^([_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))?$")
	private String email;

	@Column
	private String rg;

	@Column(name = "birth_date")
	private Date birthDate;

	@Column(name = "creation_date")
	private Date creationDate;

	@Column(name = "initial_session_count")
	private int initialSessionCount;

	public Patient() {
		initialSessionCount = 0;
		address = new Address();
		address.setZipCode("");
		creationDate = Date.from(Instant.now());
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public int getInitialSessionCount() {
		return initialSessionCount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Patient)) return false;

		Patient patient = (Patient) o;

		if (patientId != null ? !patientId.equals(patient.patientId) : patient.patientId != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return patientId != null ? patientId.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "[" + patientId + "] " + name;
	}
}