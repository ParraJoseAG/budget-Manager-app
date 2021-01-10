package com.alkemy.java.budgetManager.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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

import com.alkemy.java.budgetManager.entities.OperationEntity;
import com.alkemy.java.budgetManager.entities.PersonEntity;
import com.alkemy.java.budgetManager.exceptions.PersonNotFound;
import com.alkemy.java.budgetManager.models.Type;
import com.alkemy.java.budgetManager.service.IOperationService;
import com.alkemy.java.budgetManager.service.IPersonService;

@Controller
@RequestMapping("/operation")
public class BudgetManagerController {

	private PersonEntity personEntity = null;

	@Autowired
	private IPersonService personService;

	@Autowired
	private IOperationService operationService;

	@GetMapping("/person/{id}")
	public String home(@PathVariable("id") Long idPerson, Model model, RedirectAttributes attribute)
			throws PersonNotFound {

		try {
			personEntity = personService.getPersonById(idPerson);
		} catch (PersonNotFound e) {
			attribute.addFlashAttribute("error", e.getMessage());
			return "redirect:/people";
		}

		List<OperationEntity> listOperationsPerson = operationService.getLastTenOperation(idPerson);

		BigDecimal balance = operationService.getCurrentBalance(idPerson);

		model.addAttribute("operations", listOperationsPerson);
		model.addAttribute("person", personEntity);
		model.addAttribute("balance", balance);
		model.addAttribute("titleTable", "Últimas operaciones realizadas");
		return "budgetManager/homeOperationPerson";
	}

	@GetMapping("/addOperation")
	public String addPerson(Model model) {

		OperationEntity operationEntity = new OperationEntity();

		operationEntity.setPerson(personEntity);
		model.addAttribute("titleTable", "Nueva Operación");
		model.addAttribute("action", "CREATE");
		model.addAttribute("person", personEntity);
		model.addAttribute("operation", operationEntity);
		model.addAttribute("listTypeOperation", Type.values());

		return "budgetManager/addOperation";
	}

	@PostMapping("/saveOperation")
	public String savePerson(@ModelAttribute("operation") OperationEntity operationEntity, BindingResult result,
			Model model, RedirectAttributes attribute) {

		operationService.saveOperation(operationEntity);
		attribute.addFlashAttribute("success", "Operación registrada con éxito!");
		return "redirect:/operation/person/" + operationEntity.getPerson().getId();
	}

	@GetMapping("/person/ingress")
	public String OperationsIngress(@RequestParam Map<String, Object> params, Model model,
			RedirectAttributes attribute) {

		int page = (int) (params.get("page") != null ? (Long.valueOf(params.get("page").toString()) - 1) : 0);
		PageRequest pageRequest = PageRequest.of(page, 5);

		Page<OperationEntity> pageOperationEntity = operationService.getListOperationIngress(personEntity.getId(),
				pageRequest);

		int totalPage = pageOperationEntity.getTotalPages();

		if (totalPage > 0) {

			List<Long> pages = LongStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
			model.addAttribute("pages", pages);
		}

		List<OperationEntity> listOperationsPersonIngress = pageOperationEntity.getContent();

		BigDecimal ingress = operationService.getTotalIngress(personEntity.getId());

		model.addAttribute("operations", listOperationsPersonIngress);
		model.addAttribute("person", personEntity);
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
		PageRequest pageRequest = PageRequest.of(page, 5);

		Page<OperationEntity> pageOperationEntity = operationService.getListOperationExpenses(personEntity.getId(),
				pageRequest);

		int totalPage = pageOperationEntity.getTotalPages();

		if (totalPage > 0) {

			List<Long> pages = LongStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
			model.addAttribute("pages", pages);
		}

		List<OperationEntity> listOperationsPersonExpenses = pageOperationEntity.getContent();
		BigDecimal expenses = operationService.getTotalExpenses(personEntity.getId());

		model.addAttribute("operations", listOperationsPersonExpenses);
		model.addAttribute("person", personEntity);
		model.addAttribute("expenses", expenses);
		model.addAttribute("current", page + 1);
		model.addAttribute("next", page + 2);
		model.addAttribute("previous", page);
		model.addAttribute("last", totalPage);
		model.addAttribute("titleTable", "Operaciones de tipo egresos realizadas");
		return "budgetManager/operationsExpenses";
	}

}
