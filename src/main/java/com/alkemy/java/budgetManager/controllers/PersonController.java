package com.alkemy.java.budgetManager.controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.exceptions.PersonNotFound;
import com.alkemy.java.budgetManager.service.IPersonService;

@Controller
@RequestMapping({ "/people", "/" })
public class PersonController {

	@Autowired
	private IPersonService personService;

	@GetMapping
	public String listPeople(@RequestParam Map<String, Object> params, Model model) {

		int page = (int) (params.get("page") != null ? (Long.valueOf(params.get("page").toString()) - 1) : 0);
		PageRequest pageRequest = PageRequest.of(page, 10);

		Page<PersonEntity> pagePersonEntity = personService.getListPerson(pageRequest);

		int totalPage = pagePersonEntity.getTotalPages();

		if (totalPage > 0) {

			List<Long> pages = LongStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
			model.addAttribute("pages", pages);
		}

		model.addAttribute("listPeople", pagePersonEntity.getContent());
		model.addAttribute("titleTable", "Personas registradas en la APP");
		model.addAttribute("current", page + 1);
		model.addAttribute("next", page + 2);
		model.addAttribute("previous", page);
		model.addAttribute("last", totalPage);
		return "/viewPerson/listPeople";
	}

	@GetMapping("/addPerson")
	public String addPerson(Model model) {

		PersonEntity personEntity = new PersonEntity();

		model.addAttribute("titleTable", "Registrando una persona en la APP");
		model.addAttribute("action", "CREATE");
		model.addAttribute("person", personEntity);

		return "viewPerson/addPerson";
	}

	@PostMapping("/savePerson")
	public String savePerson(@ModelAttribute("person") PersonEntity personEntity, Model model,
			@RequestParam("file") MultipartFile image, RedirectAttributes attribute) {

		if (!image.isEmpty()) {

			try {
				personEntity.setPhoto(Base64.getEncoder().encodeToString(image.getBytes()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		personService.savePerson(personEntity);
		attribute.addFlashAttribute("success", "Persona registrada con éxito!");
		return "redirect:/people";
	}

	@GetMapping("/edit/{id}")
	public String editPerson(@PathVariable("id") Long idPerson, Model model, RedirectAttributes attribute)
			throws PersonNotFound {
		PersonEntity personEntity = null;
		try {
			personEntity = personService.getPersonById(idPerson);
		} catch (PersonNotFound e) {
			attribute.addFlashAttribute("error", e.getMessage());
			return "redirect:/people";
		}
		model.addAttribute("titleTable", "Editar Persona");
		model.addAttribute("person", personEntity);

		return "viewPerson/addPerson";
	}

	@GetMapping("/delete/{id}")
	public String deletePerson(@PathVariable("id") Long idPerson, Model model, RedirectAttributes attribute)
			throws PersonNotFound {
		PersonEntity personEntity = null;
		try {
			personEntity = personService.getPersonById(idPerson);
		} catch (PersonNotFound e) {
			attribute.addFlashAttribute("error", e.getMessage());
			return "redirect:/people";
		}

		personService.deletePersonById(idPerson);
		attribute.addFlashAttribute("warning",
				personEntity.getName() + " " + personEntity.getSurname() + " fue eliminad@ con éxito!");
		return "redirect:/people";
	}
}
