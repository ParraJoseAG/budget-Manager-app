package com.alkemy.java.budgetManager.models;

import java.math.BigDecimal;
import java.util.Date;

public class FixedTerm {

	private Long id;
	private BigDecimal amountFixedTerm;
	private BigDecimal amountGenerated;
	private Date startDate;
	private Date finalDate;
	private Person person;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmountFixedTerm() {
		return amountFixedTerm;
	}

	public void setAmountFixedTerm(BigDecimal amountFixedTerm) {
		this.amountFixedTerm = amountFixedTerm;
	}

	public BigDecimal getAmountGenerated() {
		return amountGenerated;
	}

	public void setAmountGenerated(BigDecimal amountGenerated) {
		this.amountGenerated = amountGenerated;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(Date finalDate) {
		this.finalDate = finalDate;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
