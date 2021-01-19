package com.alkemy.java.budgetManager.controllers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alkemy.java.budgetManager.Utils.FixedTermUtil;
import com.alkemy.java.budgetManager.entities.FixedTermEntity;
import com.alkemy.java.budgetManager.entities.OperationEntity;
import com.alkemy.java.budgetManager.models.Type;
import com.alkemy.java.budgetManager.service.IFixedTermService;
import com.alkemy.java.budgetManager.service.IOperationService;
import com.alkemy.java.budgetManager.service.IPersonService;

@Controller
@RequestMapping({ "/fixedTerm/person/{idUser}", "/fixedTerm/person/" })
public class FixedTermController {

	@Autowired
	private IPersonService personService;

	@Autowired
	private IFixedTermService fixedTermService;

	@Autowired
	private IOperationService operationService;

	@Autowired
	private FixedTermUtil fixedTermUtil;

	@GetMapping
	public String depositsFixed(@PathVariable(required = false) Long idUser, Model model) {

		if (idUser == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetail = (UserDetails) auth.getPrincipal();
			idUser = personService.findByUsername(userDetail.getUsername()).getId();
		}

		FixedTermEntity fixedTerm = new FixedTermEntity();
		fixedTerm.setPerson(personService.getPersonById(idUser));
		List<FixedTermEntity> listFixedTerm = fixedTermService.getListFixedTermPerson(idUser);
		BigDecimal balance = operationService.getCurrentBalance(idUser);

		model.addAttribute("listDepositsFixed", listFixedTerm);
		model.addAttribute("titleTable", "Depositos a Plazo Fijo");
		model.addAttribute("balance", balance);
		model.addAttribute("person", personService.getPersonById(idUser));
		model.addAttribute("fixedTerm", fixedTerm);

		return "fixedTerm/depositsFixed";
	}

	@PostMapping("/saveFixedTerm")
	public String saveFixedTerm(@ModelAttribute("fixedTerm") FixedTermEntity fixedTerm, RedirectAttributes attribute) {

		Long idUser = fixedTerm.getPerson().getId();
		fixedTerm.setAmountGenerated(new BigDecimal(0));
		fixedTerm.setStartDate(new Date());

		try {

			fixedTermService.saveFixedTerm(fixedTerm);

		} catch (Exception e) {
			attribute.addFlashAttribute("error",
					"No tiene suficiente dinero disponible en su balance para realizar la operación!");
			return "redirect:/fixedTerm/person/" + idUser;
		}

		OperationEntity operationFixedTerm = new OperationEntity();
		operationFixedTerm.setConcept("INVERSIÓN A PLAZO FIJO");
		operationFixedTerm.setAmount(fixedTerm.getAmountFixedTerm());
		operationFixedTerm.setDate(new Date());
		operationFixedTerm.setType(Type.EXPENSES);
		operationFixedTerm.setPerson(fixedTerm.getPerson());

		operationService.saveOperation(operationFixedTerm);
		attribute.addFlashAttribute("success", "Operación exitosa!");
		return "redirect:/fixedTerm/person/" + idUser;
	}

	@GetMapping("/endFixedTerm/{id}")
	public String endFixedTerm(@PathVariable("id") Long idFixed, Model model, RedirectAttributes attribute) {

		FixedTermEntity fixedTerm = fixedTermService.getByIdFixedTerm(idFixed);
		Long idUser = fixedTerm.getPerson().getId();

		BigDecimal amountGenerated = fixedTermUtil.getAmountGenerated(fixedTerm);

		OperationEntity operationFixedTerm = new OperationEntity();
		operationFixedTerm.setConcept("DEPOSITO DE LA INVERSIÓN");
		operationFixedTerm.setAmount(amountGenerated);
		operationFixedTerm.setDate(new Date());
		operationFixedTerm.setType(Type.INGRESS);
		operationFixedTerm.setPerson(fixedTerm.getPerson());

		fixedTermService.deleteByIdFixedTerm(idFixed);
		operationService.saveOperation(operationFixedTerm);
		attribute.addFlashAttribute("success", "Deposito en cuenta por plazo fijo exitoso!");
		return "redirect:/fixedTerm/person/" + idUser;

	}
}
