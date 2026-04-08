package ru.mentee.power.crm.spring.controller;

import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.dto.CreateCompanyForm;
import ru.mentee.power.crm.service.CompanyService;
import ru.mentee.power.crm.service.LeadService;

@Controller
@AllArgsConstructor
public class CompanyController {

  private final LeadService leadService;
  private final CompanyService companyService;

  // Главная страница с лидами
  @GetMapping("/companies")
  public String showCompanies(Model model) {
    model.addAttribute("companies", companyService.findAllCompanies());
    return "company/list";
  }

  // Переадресация на страницу создания нового лида
  @GetMapping("/company/new")
  public String showCreateForm(Model model) {
    model.addAttribute("companyForm", new CreateCompanyForm());
    return "company/create";
  }

  @PostMapping("/companies")
  public String createLead(
      @Valid @ModelAttribute("companyForm") CreateCompanyForm companyForm,
      BindingResult errors,
      Model model) {
    if (errors.hasErrors()) {
      model.addAttribute("companyForm", companyForm);
      model.addAttribute("errors", errors);
      return "company/create";
    }

    Optional<Company> result = companyService.addCompany(companyForm);

    if (result.isPresent()) {
      return "redirect:/companies";
    } else {
      model.addAttribute("leadAlreadyExist", true);
      model.addAttribute("leadForm", companyForm);
      return "company/create";
    }
  }

  @PostMapping("/companies/{id}/delete")
  public String deleteCompany(@PathVariable UUID id) {
    companyService.deleteCompany(id);
    return "redirect:/companies";
  }
}
