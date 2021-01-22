package com.alkemy.java.budgetManager.controllers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alkemy.java.budgetManager.Utils.FixedTermUtil;
import com.alkemy.java.budgetManager.Utils.PersonAuthenticationUtil;
import com.alkemy.java.budgetManager.entities.FixedTermEntity;
import com.alkemy.java.budgetManager.entities.OperationEntity;
import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.exceptions.FixedTermNotFoundException;
import com.alkemy.java.budgetManager.models.Type;
import com.alkemy.java.budgetManager.service.IFixedTermService;
import com.alkemy.java.budgetManager.service.IOperationService;

@Controller
@RequestMapping("/fixedTerm/person/")
public class FixedTermController {

	private PersonEntity personUser;

	@Autowired
	private IFixedTermService fixedTermService;

	@Autowired
	private IOperationService operationService;

	@Autowired
	private FixedTermUtil fixedTermUtil;

	@Autowired
	private PersonAuthenticationUtil personAuthenticationUtil;

	@GetMapping
	public String depositsFixed(Model model) {

		personUser = personAuthenticationUtil.personAuthentication();
		Long idUser = personUser.getId();

		FixedTermEntity fixedTerm = new FixedTermEntity();
		fixedTerm.setPerson(personUser);
		fixedTerm.setAmountGenerated(new BigDecimal(0));

		List<FixedTermEntity> listFixedTerm = fixedTermService.getListFixedTermPerson(idUser);

		for (FixedTermEntity fixedTermEntity : listFixedTerm) {
			fixedTermEntity.setAmountGenerated(fixedTermUtil.getAmountGenerated(fixedTermEntity));
		}

		BigDecimal balance = operationService.getCurrentBalance(idUser);

		model.addAttribute("listDepositsFixed", listFixedTerm);
		model.addAttribute("titleTable", "Depositos a Plazo Fijo");
		model.addAttribute("balance", balance);
		model.addAttribute("person", personUser);
		model.addAttribute("fixedTerm", fixedTerm);

		return "fixedTerm/depositsFixed";
	}

	@PostMapping("/saveFixedTerm")
	public String saveFixedTerm(@ModelAttribute FixedTermEntity fixedTerm, Model model, RedirectAttributes attribute) {

		fixedTerm.setStartDate(new Date());

		try {

			fixedTermService.saveFixedTerm(fixedTerm);

		} catch (Exception e) {
			attribute.addFlashAttribute("error",
					"No tiene suficiente dinero disponible en su balance para realizar la operación!");
			return "redirect:/fixedTerm/person/";
		}

		OperationEntity operationFixedTerm = fixedTermUtil.getOperation(Type.EXPENSES, fixedTerm);

		try {
			operationService.saveOperation(operationFixedTerm);
		} catch (Exception e) {
			attribute.addFlashAttribute("error",
					"No tiene suficiente dinero disponible en su balance para realizar la operación!");
			return "redirect:/fixedTerm/person/";
		}
		attribute.addFlashAttribute("success", "Operación exitosa!");
		return "redirect:/fixedTerm/person/";
	}

	@GetMapping("/endFixedTerm/{id}")
	public String endFixedTerm(@PathVariable("id") Long idFixed, Model model, RedirectAttributes attribute) {

		FixedTermEntity fixedTerm = null;

		try {
			fixedTerm = fixedTermService.getByIdFixedTerm(idFixed);
		} catch (FixedTermNotFoundException e) {
			attribute.addFlashAttribute("error", e.getMessage());
			return "redirect:/fixedTerm/person/";
		}

		OperationEntity operationFixedTerm = fixedTermUtil.getOperation(Type.INGRESS, fixedTerm);

		fixedTermService.deleteByIdFixedTerm(idFixed);
		try {
			operationService.saveOperation(operationFixedTerm);
		} catch (Exception e) {
			attribute.addFlashAttribute("error",
					"No tiene suficiente dinero disponible en su balance para realizar la operación!");
			return "redirect:/fixedTerm/person/";
		}

		attribute.addFlashAttribute("success", "Deposito en cuenta por plazo fijo exitoso!");
		return "redirect:/fixedTerm/person/";

	}
}
