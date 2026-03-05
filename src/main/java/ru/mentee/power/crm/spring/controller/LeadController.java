package ru.mentee.power.crm.spring.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

@Controller
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
        for (int i = 0; i < 5; i++) {
            leadService.addLead("example" + i + "@gmail.com", "Company" + i, LeadStatus.NEW);
        }
    }

    // Главная страница с лидами
    @GetMapping("/leads")
    public String showLeads(
            @RequestParam(required = false) LeadStatus status,
            Model model
    ) {
        List<Lead> leads;
        if (status == null) {
            leads = leadService.findAll();
        } else {
            leads = leadService.findByStatus(status);
            model.addAttribute("currentFilter", status);
        }

        model.addAttribute("leads", leads);
        model.addAttribute("leadNotFound", false);
        return "leads/list";
    }

    // Переадресация на страницу создания нового лида
    @GetMapping("/leads/new")
    public String showCreateForm(Model model) {
        // Просто создаём пустую форму — данные после ошибки
        // придут через redirect attributes или flash scope
        model.addAttribute("lead", new Lead("", "", LeadStatus.NEW));
        return "leads/create";
    }

    @PostMapping("/leads")
    public String createLead(
            @RequestParam String email,
            @RequestParam String company,
            @RequestParam LeadStatus status,
            Model model
    ) {
        Optional<Lead> result = leadService.addLead(email, company, status);
        if (result.isPresent()) {
            return "redirect:/leads";
        } else {
            // Передаём введённые данные обратно в форму
            model.addAttribute("leadAlreadyExist", true);
            model.addAttribute("lead", new Lead(email, company, status));
            return "leads/create";
        }
    }

    // Переадресация на страницу редактирования лида
    @GetMapping("/leads/{id}/edit")
    public String showLeadUpdating(@PathVariable UUID id, Model model) {
        Optional<Lead> lead = leadService.findById(id);
        if (lead.isPresent()) {
            model.addAttribute("lead", lead.get());
            return "leads/edit";
        } else {
            model.addAttribute("leads", leadService.findAll());
            model.addAttribute("leadNotFound", true);
            model.addAttribute("currentFilter", null);
            return "leads/list";
        }
    }

    @PostMapping("/leads/{id}/edit")
    public String updateLead(
            @PathVariable UUID id,
            @RequestParam String email,
            @RequestParam String company,
            @RequestParam LeadStatus status
    ) {
        leadService.updateLead(id, email, company, status);
        return "redirect:/leads";
    }

    @PostMapping("/leads/{id}/delete")
    public String deleteLead(@PathVariable UUID id) {
        leadService.deleteLead(id);
        return "redirect:/leads";
    }
}
