package com.alkemy.java.budgetManager.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.exceptions.AuthorityNotFoundException;
import com.alkemy.java.budgetManager.service.IPersonService;

@Controller
@RequestMapping("/auth")
public class LoginController {

	@Autowired
	private IPersonService personService;

	@GetMapping("/register")
	public String registerForm(Model model) {

		model.addAttribute("person", new PersonEntity());
		return "/login/register";
	}

	@PostMapping("/saveRegister")
	public String register(@Valid @ModelAttribute("person") PersonEntity personUser, BindingResult result, Model model,
			RedirectAttributes attribute) throws AuthorityNotFoundException {

		if (result.hasErrors()) {
			model.addAttribute("person", personUser);
			return "/login/register";
		}

		if (personService.findByUsername(personUser.getUsername()) != null) {
			model.addAttribute("message", "Ya existe un usuario con el nombre de usuario dado.");
			model.addAttribute("person", personUser);
			return "/login/register";
		}

		if (personService.findByEmail(personUser.getEmail()) != null) {
			model.addAttribute("message", "Ya existe un usuario con la dirección de correo dada.");
			model.addAttribute("person", personUser);
			return "/login/register";
		}
		try {
			personService.savePerson(personUser);
		} catch (AuthorityNotFoundException e) {
			attribute.addFlashAttribute("error", "Error al registrarte");
			return "redirect:/";
		}

		attribute.addFlashAttribute("success",
				"Felicitaciones " + personUser.getName() + " ya eres parte de la comunidad");
		model.addAttribute("person", personUser);
		return "redirect:/";

	}

}
