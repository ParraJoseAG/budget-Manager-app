package com.alkemy.java.budgetManager.controllers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alkemy.java.budgetManager.Utils.PersonAuthenticationUtil;
import com.alkemy.java.budgetManager.entities.OperationEntity;
import com.alkemy.java.budgetManager.entities.PendingPaymentEntity;
import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.exceptions.PendingPaymentNotFoundException;
import com.alkemy.java.budgetManager.models.Type;
import com.alkemy.java.budgetManager.service.IOperationService;
import com.alkemy.java.budgetManager.service.IPendingPaymentService;

@Controller
@RequestMapping("/pendingPayments")
public class PendingPaymentController {

	private PersonEntity personUser;

	@Autowired
	private PersonAuthenticationUtil personAuthenticationUtil;

	@Autowired
	private IPendingPaymentService pendingPaymentService;

	@Autowired
	private IOperationService operationService;

	@GetMapping
	public String listPendingPayment(Model model) {

		personUser = personAuthenticationUtil.personAuthentication();
		Long idUser = personUser.getId();

		List<PendingPaymentEntity> listPendingPayments = pendingPaymentService.listPendingPayments(idUser);
		BigDecimal balance = operationService.getCurrentBalance(idUser);

		model.addAttribute("pendingPayments", listPendingPayments);
		model.addAttribute("person", personUser);
		model.addAttribute("balance", balance);
		model.addAttribute("titleTable", "PAGOS PENDIENTES");

		return "/pendingPayments/pendingPayments";

	}

	@GetMapping("/addPendingPayment")
	public String addOperation(Model model) {

		personUser = personAuthenticationUtil.personAuthentication();

		PendingPaymentEntity pendingPayment = new PendingPaymentEntity();

		pendingPayment.setPerson(personUser);
		model.addAttribute("titleTable", "AGENDAR PAGO");
		model.addAttribute("person", personUser);
		model.addAttribute("pendingPayment", pendingPayment);

		return "/pendingPayments/addpendingPayment";
	}

	@PostMapping("/savePendingPayment")
	public String savePendingPayment(@Valid @ModelAttribute("pendingPayment") PendingPaymentEntity pendingPayment,
			BindingResult result, Model model, RedirectAttributes attribute) {

		personUser = personAuthenticationUtil.personAuthentication();

		if (result.hasErrors()) {
			model.addAttribute("titleTable", "AGENDAR PAGO");
			model.addAttribute("person", personUser);
			model.addAttribute("pendingPayment", pendingPayment);

			return "pendingPayments/addpendingPayment";
		}

		pendingPaymentService.createPendingPayment(pendingPayment);
		attribute.addFlashAttribute("success", "PAGO PENDIENTE AGENDADO CON ÉXITO!");
		return "redirect:/pendingPayments";
	}

	@GetMapping("/editPendingPayment/{id}")
	public String editPendingPayment(@PathVariable("id") Long idPendingPayment, Model model,
			RedirectAttributes attribute) {

		personUser = personAuthenticationUtil.personAuthentication();
		PendingPaymentEntity pendingPayment = null;
		try {
			pendingPayment = pendingPaymentService.findPendingPaymentEntityById(idPendingPayment);
		} catch (PendingPaymentNotFoundException e) {
			attribute.addFlashAttribute("error", "PAGO PENDIENTE CON ID " + idPendingPayment + " NO ESTA AGENDADO!");
			return "redirect:/pendingPayments";
		}

		model.addAttribute("titleTable", "EDITAR PAGO");
		model.addAttribute("person", personUser);
		model.addAttribute("pendingPayment", pendingPayment);

		return "pendingPayments/addpendingPayment";
	}

	@GetMapping("/deletePendingPayment/{id}")
	public String deletePendingPayment(@PathVariable("id") Long idPendingPayment, Model model,
			RedirectAttributes attribute) {

		personUser = personAuthenticationUtil.personAuthentication();
		PendingPaymentEntity pendingPayment = null;
		try {
			pendingPayment = pendingPaymentService.findPendingPaymentEntityById(idPendingPayment);
		} catch (PendingPaymentNotFoundException e) {
			attribute.addFlashAttribute("error", "PAGO PENDIENTE CON ID " + idPendingPayment + " NO ESTA AGENDADO!");
			return "redirect:/pendingPayments";
		}

		pendingPaymentService.deletePendingPaymentEntityById(idPendingPayment);
		attribute.addFlashAttribute("success",
				"PAGO PENDIENTE POR CONCEPTO " + pendingPayment.getConcept() + " FUE ELIMINADO CON ÉXITO!");
		return "redirect:/pendingPayments";
	}

	@GetMapping("/payPendingPayment/{id}")
	public String payPendingPayment(@PathVariable("id") Long idPendingPayment, Model model,
			RedirectAttributes attribute) {

		personUser = personAuthenticationUtil.personAuthentication();
		PendingPaymentEntity pendingPayment = null;
		try {
			pendingPayment = pendingPaymentService.findPendingPaymentEntityById(idPendingPayment);

		} catch (PendingPaymentNotFoundException e) {
			attribute.addFlashAttribute("error", "PAGO PENDIENTE CON ID " + idPendingPayment + " NO ESTA AGENDADO!");
			return "redirect:/pendingPayments";
		}

		OperationEntity operationPay = new OperationEntity();
		operationPay.setConcept(pendingPayment.getConcept());
		operationPay.setAmount(pendingPayment.getAmount());
		operationPay.setDate(new Date());
		operationPay.setPerson(personUser);
		operationPay.setType(Type.EXPENSES);

		try {
			operationService.saveOperation(operationPay);

		} catch (Exception e) {
			attribute.addFlashAttribute("error",
					"No tiene suficiente dinero disponible en su balance para realizar la operación!");
			return "redirect:/pendingPayments";
		}
		pendingPaymentService.deletePendingPaymentEntityById(idPendingPayment);
		attribute.addFlashAttribute("success", "PAGO REALIZADO CON ÉXITO!");
		return "redirect:/pendingPayments";

	}

}
