package com.alkemy.java.budgetManager.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "people")
public class PersonEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;
	@Column
	@NotBlank
	@Pattern(regexp = "[A-Za-zÁÉÍÓÚáéíóúñÑ\s]*")
	private String name;
	@Column
	@NotBlank
	@Pattern(regexp = "[A-Za-zÁÉÍÓÚáéíóúñÑ\s]*")
	private String surname;
	@Column
	@Min(15)
	@Max(100)
	private int age;
	@Column
	@Pattern(regexp = "^[0-9]*$")
	private String dni;
	@Column
	@NotBlank
	@Email
	private String email;
	@Column
	@Pattern(regexp = "^[0-9,+,\s]*$")
	private String phoneNumber;
	@Column
	private String direction;
	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private String photo;
	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<OperationEntity> operations;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public List<OperationEntity> getOperations() {
		return operations;
	}

	public void setOperations(List<OperationEntity> operations) {
		this.operations = operations;
	}

}
