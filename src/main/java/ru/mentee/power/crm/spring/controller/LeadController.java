package ru.mentee.power.crm.spring.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    }

    @GetMapping("/leads/new")
    public String showCreateForm(Model model) {
        model.addAttribute("lead", new Lead("", "", LeadStatus.NEW));
        return "leads/create"; // JTE шаблон leads/create.jte
    }

    // Обработчик POST формы
    @PostMapping("/leads")
    public String createLead(
            @RequestParam String email,
            @RequestParam String company,
            @RequestParam LeadStatus status
    ) {
        leadService.addLead(email, company, status);
        return "redirect:/leads";  // После создания возвращаемся к списку
    }

    @GetMapping("/leads")
    public String showLeads(Model model) {
        List<Lead> leads = leadService.findAll();
        model.addAttribute("leads", leads);
        return "leads/list";
    }
}
