package com.alkemy.java.budgetManager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping
	public String homeWebSite() {

		return "home";
	}

}
