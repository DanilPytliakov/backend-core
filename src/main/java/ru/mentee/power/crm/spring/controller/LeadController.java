package ru.mentee.power.crm.spring.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.dto.CreateLeadForm;
import ru.mentee.power.crm.dto.UpdateLeadForm;
import ru.mentee.power.crm.service.DealService;
import ru.mentee.power.crm.service.LeadService;

@Controller
public class LeadController {

    private final LeadService leadService;
    private final DealService dealService;

    public LeadController(LeadService leadService, DealService dealService) {
        this.leadService = leadService;
        this.dealService = dealService;
    }

    @PostConstruct
    public void init() {
        leadService.addLead("Иван", "user1@gmail.com", "FirstCorp", LeadStatus.NEW);
        leadService.addLead("Максим", "user2@gmail.com", "FirstCorp", LeadStatus.CONTACTED);
        leadService.addLead("Виктор", "user3@gmail.com", "SecondCorp", LeadStatus.QUALIFIED);
        leadService.addLead("Мария", "user4@gmail.com", "SecondCorp", LeadStatus.NEW);
        leadService.addLead("Татьяна", "user5@gmail.com", "ThirdCorp", LeadStatus.CONTACTED);

        // Сделки — используем реальные UUID лидов
        List<Lead> leads = leadService.findAll();
        dealService.convertLeadToDeal(leads.get(0).id(), BigDecimal.valueOf(15000));
        dealService.convertLeadToDeal(leads.get(1).id(), BigDecimal.valueOf(3000));
        dealService.convertLeadToDeal(leads.get(2).id(), BigDecimal.valueOf(30000));
    }

    // Главная страница с лидами
    @GetMapping("/leads")
    public String showLeads(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) LeadStatus status,
            Model model
    ) {
        model.addAttribute("leads", leadService.findByFilter(name, email, company, status));
        model.addAttribute("currentName", name);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentEmail", email);
        model.addAttribute("currentCompany", company);
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
            Model model
    ) {
        if (errors.hasErrors()) {
            model.addAttribute("leadForm", leadForm);
            model.addAttribute("errors", errors);
            return "leads/create";
        }

        Optional<Lead> result = leadService.addLead(
                leadForm.getName(),
                leadForm.getEmail(),
                leadForm.getCompany(),
                leadForm.getStatus());

        if (result.isPresent()) {
            return "redirect:/leads";
        } else {
            model.addAttribute("leadAlreadyExist", true);
            model.addAttribute("leadForm", leadForm);
            return "leads/create";
        }
    }

    // Переадресация на страницу редактирования лида
    @GetMapping("/leads/{id}/edit")
    public String showLeadUpdating(@PathVariable UUID id, Model model) {
        Optional<Lead> lead = leadService.findById(id);
        if (lead.isPresent()) {
            model.addAttribute("leadForm", new UpdateLeadForm(lead.get()));
            return "leads/edit";

        } else {
            model.addAttribute("leads", leadService.findAll());
            model.addAttribute("leadNotFound", true);
            model.addAttribute("currentStatus", null);
            return "leads/list";
        }
    }

    @PostMapping("/leads/{id}/edit")
    public String updateLead(
            @Valid @ModelAttribute("leadForm") UpdateLeadForm leadForm,
            BindingResult errors,
            Model model
    ) {
        if (errors.hasErrors()) {
            model.addAttribute("leadForm", leadForm);
            model.addAttribute("errors", errors);
            return "leads/edit";
        } else {
            leadService.updateLead(
                    leadForm.getId(),
                    leadForm.getName(),
                    leadForm.getEmail(),
                    leadForm.getCompany(),
                    leadForm.getStatus());
            return "redirect:/leads";
        }
    }

    @PostMapping("/leads/{id}/delete")
    public String deleteLead(@PathVariable UUID id) {
        leadService.deleteLead(id);
        return "redirect:/leads";
    }
}
