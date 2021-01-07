package com.alkemy.java.budgetManager.models;

public enum Type {

	INGRESS("INGRESO"), EXPENSES("EGRESO");

	private final String typeOperation;

	private Type(String typeOperation) {
		this.typeOperation = typeOperation;
	}

	public String getTypeOperation() {
		return typeOperation;
	}

}
