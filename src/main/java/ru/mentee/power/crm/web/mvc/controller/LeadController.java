package ru.mentee.power.crm.web.mvc.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.service.CompanyService;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.web.mvc.dto.CreateLeadForm;
import ru.mentee.power.crm.web.mvc.dto.UpdateLeadForm;

@Controller
@AllArgsConstructor
public class LeadController {

  private final LeadService leadService;
  private final CompanyService companyService;

  // Автоматически добавляется в модель для всех методов этого контроллера
  @ModelAttribute("companies")
  public List<Company> getAllCompanies() {
    return companyService.findAllCompanies();
  }

  @GetMapping("/leads")
  public String showLeads(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String companyName,
      @RequestParam(required = false) String industry, // было companyIndustry
      @RequestParam(required = false) LeadStatus status,
      Model model) {
    model.addAttribute("industries", companyService.findAllIndustries());
    model.addAttribute(
        "leads", leadService.findByFilter(name, email, companyName, industry, status));
    model.addAttribute("currentName", name);
    model.addAttribute("currentStatus", status);
    model.addAttribute("currentEmail", email);
    model.addAttribute("currentCompanyName", companyName);
    model.addAttribute("currentCompanyIndustry", industry);
    model.addAttribute("leadNotFound", false);
    return "leads/list";
  }

  // Переадресация на страницу создания нового лида
  @GetMapping("/leads/new")
  public String showCreateForm(Model model) {
    model.addAttribute("leadForm", new CreateLeadForm());
    return "leads/create";
  }

  @PostMapping("/leads")
  public String createLead(
      @Valid @ModelAttribute("leadForm") CreateLeadForm leadForm,
      BindingResult errors,
      Model model) {
    if (errors.hasErrors()) {
      model.addAttribute("leadForm", leadForm);
      model.addAttribute("errors", errors);
      return "leads/create";
    }

    UUID companyId = leadForm.getCompanyId();

    Company company = null;

    if (companyId != null) {
      company = companyService.findById(companyId).get();
    }

    Optional<Lead> result =
        leadService.addLead(leadForm.getName(), leadForm.getEmail(), company, leadForm.getStatus());
    if (result.isPresent()) {
      return "redirect:/leads";
    } else {
      model.addAttribute("leadAlreadyExist", true);
      model.addAttribute("leadForm", leadForm);
      return "leads/create";
    }
  }

  @GetMapping("/leads/{id}/edit")
  public String showLeadUpdating(@PathVariable UUID id, Model model) {
    Optional<Lead> lead = leadService.findById(id);
    if (lead.isPresent()) {
      model.addAttribute("leadForm", new UpdateLeadForm(lead.get()));
      model.addAttribute("companies", companyService.findAllCompanies());
      return "leads/edit";
    } else {
      model.addAttribute("leads", leadService.findAll());
      model.addAttribute("companies", companyService.findAllCompanies());
      model.addAttribute("industries", companyService.findAllIndustries());
      model.addAttribute("leadNotFound", true);
      model.addAttribute("currentName", null);
      model.addAttribute("currentEmail", null);
      model.addAttribute("currentCompanyName", null);
      model.addAttribute("currentCompanyIndustry", null);
      model.addAttribute("currentStatus", null);
      return "leads/list";
    }
  }

  @PostMapping("/leads/{id}/edit")
  public String updateLead(
      @Valid @ModelAttribute("leadForm") UpdateLeadForm leadForm,
      BindingResult errors,
      Model model) {
    if (errors.hasErrors()) {
      model.addAttribute("leadForm", leadForm);
      model.addAttribute("errors", errors);
      return "leads/edit";
    } else {
      Company company = null;

      if (leadForm.getCompanyId() != null) {
        company = companyService.findById(leadForm.getCompanyId()).get();
      }

      leadService.updateLead(
          leadForm.getId(), leadForm.getName(), leadForm.getEmail(), company, leadForm.getStatus());
      return "redirect:/leads";
    }
  }

  @PostMapping("/leads/{id}/delete")
  public String deleteLead(@PathVariable UUID id) {
    leadService.deleteLead(id);
    return "redirect:/leads";
  }
}
