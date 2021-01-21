package com.alkemy.java.budgetManager.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.service.IPersonService;

@Component
public class PersonAuthenticationUtil {

	@Autowired
	private IPersonService personService;

	public PersonEntity personAuthentication() {

		PersonEntity personUser = new PersonEntity();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			UserDetails userDetail = (UserDetails) auth.getPrincipal();
			personUser = personService.findByUsername(userDetail.getUsername());
		}

		return personUser;

	}

}
