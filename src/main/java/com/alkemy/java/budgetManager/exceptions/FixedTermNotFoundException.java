package com.alkemy.java.budgetManager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FixedTermNotFoundException extends RuntimeException {

	public FixedTermNotFoundException(String menssage) {
		super(menssage);
	}

	private static final long serialVersionUID = 1L;
}
