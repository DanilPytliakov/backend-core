package ru.mentee.power.crm.spring.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.model.CreateLeadForm;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.model.UpdateLeadForm;
import ru.mentee.power.crm.service.LeadService;

@Controller
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostConstruct
    public void init() {
        leadService.addLead("user1@gmail.com", "FirstCorp", LeadStatus.NEW);
        leadService.addLead("user2@gmail.com", "FirstCorp", LeadStatus.CONTACTED);
        leadService.addLead("user3@gmail.com", "SecondCorp", LeadStatus.QUALIFIED);
        leadService.addLead("user4@gmail.com", "SecondCorp", LeadStatus.NEW);
        leadService.addLead("user5@gmail.com", "ThirdCorp", LeadStatus.CONTACTED);
    }

    // Главная страница с лидами
    @GetMapping("/leads")
    public String showLeads(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) LeadStatus status,
            Model model
    ) {
        model.addAttribute("leads", leadService.findByFilter(email, company, status));
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
            model.addAttribute("leadForm", new UpdateLeadForm(
                    lead.get().id(),
                    lead.get().email(),
                    lead.get().company(),
                    lead.get().status()));
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
        }
        else {
            leadService.updateLead(leadForm.getId(),
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
