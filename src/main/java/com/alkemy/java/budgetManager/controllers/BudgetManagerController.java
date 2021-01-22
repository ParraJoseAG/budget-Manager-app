package com.alkemy.java.budgetManager.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alkemy.java.budgetManager.Utils.PersonAuthenticationUtil;
import com.alkemy.java.budgetManager.entities.OperationEntity;
import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.exceptions.PersonNotFoundException;
import com.alkemy.java.budgetManager.models.Type;
import com.alkemy.java.budgetManager.service.IOperationService;
import com.alkemy.java.budgetManager.service.IPersonService;

@Controller
@RequestMapping("/operation")
public class BudgetManagerController {

	private PersonEntity personUser;

	@Autowired
	private IPersonService personService;

	@Autowired
	private IOperationService operationService;

	@Autowired
	private PersonAuthenticationUtil personAuthenticationUtil;

	@GetMapping("/person")
	public String homeOperationsPerson(Model model, RedirectAttributes attribute) {

		personUser = personAuthenticationUtil.personAuthentication();
		Long idUser = personUser.getId();

		List<OperationEntity> listOperationsPerson = operationService.getLastTenOperation(idUser);
		BigDecimal balance = operationService.getCurrentBalance(idUser);

		model.addAttribute("operations", listOperationsPerson);
		model.addAttribute("person", personUser);
		model.addAttribute("balance", balance);
		model.addAttribute("titleTable", "Últimas operaciones realizadas");
		return "budgetManager/homeOperationPerson";
	}

	@GetMapping("/addOperation")
	public String addOperation(Model model) {

		personUser = personAuthenticationUtil.personAuthentication();

		OperationEntity operationEntity = new OperationEntity();

		operationEntity.setPerson(personUser);
		model.addAttribute("titleTable", "Nueva Operación");
		model.addAttribute("person", personUser);
		model.addAttribute("operation", operationEntity);
		model.addAttribute("listTypeOperation", Type.values());

		return "budgetManager/addOperation";
	}

	@PostMapping("/saveOperation")
	public String saveOperation(@Valid @ModelAttribute("operation") OperationEntity operationEntity,
			BindingResult result, Model model, RedirectAttributes attribute) {

		personUser = personAuthenticationUtil.personAuthentication();

		if (result.hasErrors()) {
			model.addAttribute("titleTable", "Nueva Operación");
			model.addAttribute("person", personUser);
			model.addAttribute("operation", operationEntity);
			model.addAttribute("listTypeOperation", Type.values());

			return "budgetManager/addOperation";
		}

		try {
			operationService.saveOperation(operationEntity);
		} catch (Exception e) {
			attribute.addFlashAttribute("error",
					"No tiene suficiente dinero disponible en su balance para realizar la operación!");
			return "redirect:/operation/person/";
		}
		attribute.addFlashAttribute("success", "Operación registrada con éxito!");
		return "redirect:/operation/person/";
	}

	@GetMapping("/person/ingress")
	public String operationsIngress(@RequestParam Map<String, Object> params, Model model,
			RedirectAttributes attribute) {

		personUser = personAuthenticationUtil.personAuthentication();

		int page = (int) (params.get("page") != null ? (Long.valueOf(params.get("page").toString()) - 1) : 0);
		PageRequest pageRequest = PageRequest.of(page, 10);
		Long idUser = personUser.getId();

		Page<OperationEntity> pageOperationEntity = operationService.getListOperationIngress(idUser, pageRequest);

		int totalPage = pageOperationEntity.getTotalPages();

		if (totalPage > 0) {

			List<Long> pages = LongStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
			model.addAttribute("pages", pages);
		}

		List<OperationEntity> listOperationsPersonIngress = pageOperationEntity.getContent();
		BigDecimal ingress = operationService.getTotalIngress(idUser);

		model.addAttribute("operations", listOperationsPersonIngress);
		model.addAttribute("person", personUser);
		model.addAttribute("ingress", ingress);
		model.addAttribute("current", page + 1);
		model.addAttribute("next", page + 2);
		model.addAttribute("previous", page);
		model.addAttribute("last", totalPage);
		model.addAttribute("titleTable", "Operaciones de tipo ingresos realizadas");
		return "budgetManager/homeOperationPerson";
	}

	@GetMapping("/person/expenses")
	public String operationsExpenses(@RequestParam Map<String, Object> params, Model model,
			RedirectAttributes attribute) {

		personUser = personAuthenticationUtil.personAuthentication();

		int page = (int) (params.get("page") != null ? (Long.valueOf(params.get("page").toString()) - 1) : 0);
		PageRequest pageRequest = PageRequest.of(page, 10);
		Long idUser = personUser.getId();

		Page<OperationEntity> pageOperationEntity = operationService.getListOperationExpenses(idUser, pageRequest);

		int totalPage = pageOperationEntity.getTotalPages();

		if (totalPage > 0) {

			List<Long> pages = LongStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
			model.addAttribute("pages", pages);
		}

		List<OperationEntity> listOperationsPersonExpenses = pageOperationEntity.getContent();
		BigDecimal expenses = operationService.getTotalExpenses(idUser);

		model.addAttribute("operations", listOperationsPersonExpenses);
		model.addAttribute("person", personUser);
		model.addAttribute("expenses", expenses);
		model.addAttribute("current", page + 1);
		model.addAttribute("next", page + 2);
		model.addAttribute("previous", page);
		model.addAttribute("last", totalPage);
		model.addAttribute("titleTable", "Operaciones de tipo egresos realizadas");
		return "budgetManager/operationsExpenses";
	}

	@GetMapping("/person/listUsers")
	public String listPeople(@RequestParam Map<String, Object> params, Model model) {

		personUser = personAuthenticationUtil.personAuthentication();

		int page = (int) (params.get("page") != null ? (Long.valueOf(params.get("page").toString()) - 1) : 0);
		PageRequest pageRequest = PageRequest.of(page, 10);
		Long idUser = personUser.getId();

		Page<PersonEntity> pagePersonUsers = personService.getListPerson(pageRequest);

		int totalPage = pagePersonUsers.getTotalPages();

		if (totalPage > 0) {

			List<Long> pages = LongStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
			model.addAttribute("pages", pages);
		}

		List<PersonEntity> listPersonUsers = pagePersonUsers.getContent().stream()
				.filter(user -> user.getId().longValue() != idUser.longValue()).collect(Collectors.toList());
		BigDecimal balance = operationService.getCurrentBalance(idUser);

		model.addAttribute("listUsers", listPersonUsers);
		model.addAttribute("person", personUser);
		model.addAttribute("titleTable", "Usuarios registradas en la App");
		model.addAttribute("balance", balance);
		model.addAttribute("current", page + 1);
		model.addAttribute("next", page + 2);
		model.addAttribute("previous", page);
		model.addAttribute("last", totalPage);
		return "budgetManager/listUsers";
	}

	@GetMapping("/sendingMoney/{id}")
	public String sendingMoney(@PathVariable("id") Long idUserSend, Model model, RedirectAttributes attribute) {
		personUser = personAuthenticationUtil.personAuthentication();

		OperationEntity operationEntity = new OperationEntity();
		PersonEntity personBeneficiary = null;
		try {
			personBeneficiary = personService.getPersonById(idUserSend);
		} catch (PersonNotFoundException e) {
			attribute.addFlashAttribute("error", e.getMessage());
			return "redirect:/operation/person/listUsers/";
		}

		operationEntity.setPerson(personBeneficiary);

		model.addAttribute("operation", operationEntity);
		model.addAttribute("titleTable",
				"Enviar dinero a " + personBeneficiary.getName() + " " + personBeneficiary.getSurname());
		model.addAttribute("person", personUser);

		return "budgetManager/sendMoney";
	}

	@PostMapping("/saveSendingMoney")
	public String saveSendMoney(@ModelAttribute("operation") OperationEntity operationEntity, Model model,
			RedirectAttributes attribute) {

		personUser = personAuthenticationUtil.personAuthentication();
		BigDecimal receiveMoneyPotential = operationEntity.getAmount();

		OperationEntity operationUser = new OperationEntity();
		operationUser.setAmount(receiveMoneyPotential);
		operationUser.setPerson(personUser);

		try {
			operationService.saveSendingMoney(operationUser);
		} catch (Exception e) {
			attribute.addFlashAttribute("error",
					"No tiene suficiente dinero disponible en su balance para enviar la cantidad solicitada!");
			return "redirect:/operation/person/listUsers/";
		}
		operationService.saveReceiveMoney(operationEntity);
		attribute.addFlashAttribute("success", "El envio de dinero ha sido exitoso!");
		return "redirect:/operation/person/";
	}

}
