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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alkemy.java.budgetManager.entities.OperationEntity;
import com.alkemy.java.budgetManager.entities.PersonEntity;
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

	@GetMapping("/person")
	public String homePerson(Model model, RedirectAttributes attribute) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			UserDetails userDetail = (UserDetails) auth.getPrincipal();
			personUser = personService.findByUsername(userDetail.getUsername());
		}
		List<OperationEntity> listOperationsPerson = operationService.getLastTenOperation(personUser.getId());
		BigDecimal balance = operationService.getCurrentBalance(personUser.getId());

		model.addAttribute("operations", listOperationsPerson);
		model.addAttribute("person", personUser);
		model.addAttribute("balance", balance);
		model.addAttribute("titleTable", "Últimas operaciones realizadas");
		return "budgetManager/homeOperationPerson";
	}

	@GetMapping("/addOperation")
	public String addOperation(Model model) {

		OperationEntity operationEntity = new OperationEntity();

		operationEntity.setPerson(personUser);
		model.addAttribute("titleTable", "Nueva Operación");
		model.addAttribute("person", personUser);
		model.addAttribute("operation", operationEntity);
		model.addAttribute("listTypeOperation", Type.values());

		return "budgetManager/addOperation";
	}

	@PostMapping("/saveOperation")
	public String savePerson(@Valid @ModelAttribute("operation") OperationEntity operationEntity, BindingResult result,
			Model model, RedirectAttributes attribute) {

		if (result.hasErrors()) {
			model.addAttribute("titleTable", "Nueva Operación");
			model.addAttribute("person", personUser);
			model.addAttribute("operation", operationEntity);
			model.addAttribute("listTypeOperation", Type.values());

			return "budgetManager/addOperation";
		}

		operationService.saveOperation(operationEntity);
		attribute.addFlashAttribute("success", "Operación registrada con éxito!");
		return "redirect:/operation/person/";
	}

	@GetMapping("/person/ingress")
	public String OperationsIngress(@RequestParam Map<String, Object> params, Model model,
			RedirectAttributes attribute) {

		int page = (int) (params.get("page") != null ? (Long.valueOf(params.get("page").toString()) - 1) : 0);
		PageRequest pageRequest = PageRequest.of(page, 10);

		Page<OperationEntity> pageOperationEntity = operationService.getListOperationIngress(personUser.getId(),
				pageRequest);

		int totalPage = pageOperationEntity.getTotalPages();

		if (totalPage > 0) {

			List<Long> pages = LongStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
			model.addAttribute("pages", pages);
		}

		List<OperationEntity> listOperationsPersonIngress = pageOperationEntity.getContent();

		BigDecimal ingress = operationService.getTotalIngress(personUser.getId());

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
	public String OperationsExpenses(@RequestParam Map<String, Object> params, Model model,
			RedirectAttributes attribute) {

		int page = (int) (params.get("page") != null ? (Long.valueOf(params.get("page").toString()) - 1) : 0);
		PageRequest pageRequest = PageRequest.of(page, 10);

		Page<OperationEntity> pageOperationEntity = operationService.getListOperationExpenses(personUser.getId(),
				pageRequest);

		int totalPage = pageOperationEntity.getTotalPages();

		if (totalPage > 0) {

			List<Long> pages = LongStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
			model.addAttribute("pages", pages);
		}

		List<OperationEntity> listOperationsPersonExpenses = pageOperationEntity.getContent();
		BigDecimal expenses = operationService.getTotalExpenses(personUser.getId());

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

		int page = (int) (params.get("page") != null ? (Long.valueOf(params.get("page").toString()) - 1) : 0);
		PageRequest pageRequest = PageRequest.of(page, 10);

		Page<PersonEntity> pagePersonUsers = personService.getListPerson(pageRequest);

		int totalPage = pagePersonUsers.getTotalPages();

		if (totalPage > 0) {

			List<Long> pages = LongStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
			model.addAttribute("pages", pages);
		}

		List<PersonEntity> listPersonUsers = pagePersonUsers.getContent().stream()
				.filter(user -> user.getId().longValue() != this.personUser.getId().longValue())
				.collect(Collectors.toList());

		model.addAttribute("listUsers", listPersonUsers);
		model.addAttribute("person", personUser);
		model.addAttribute("titleTable", "Usuarios registradas en la App");
		model.addAttribute("current", page + 1);
		model.addAttribute("next", page + 2);
		model.addAttribute("previous", page);
		model.addAttribute("last", totalPage);
		return "budgetManager/listUsers";
	}
}
