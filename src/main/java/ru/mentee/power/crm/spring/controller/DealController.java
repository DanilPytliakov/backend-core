package ru.mentee.power.crm.spring.controller;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.dto.ConvertLeadForm;
import ru.mentee.power.crm.service.DealService;
import ru.mentee.power.crm.service.LeadService;

@Controller
@RequestMapping("/deals")
public class DealController {
    private final DealService dealService;
    private final LeadService leadService;

    public DealController(DealService dealService, LeadService leadService) {
        this.dealService = dealService;
        this.leadService = leadService;
    }

    @GetMapping
    public String listDeals(Model model) {
        model.addAttribute("deals", dealService.getAllDeals());
        return "deals/list";
    }

    @GetMapping("/kanban")
    public String kanbanView(Model model) {
        model.addAttribute("dealsByStatus", dealService.getDealsByStatusForKanban());
        return "deals/kanban";
    }

    @GetMapping("/convert/{leadId}")
    public String showConvertForm(@PathVariable UUID leadId, Model model) {
        model.addAttribute("leadForm", new ConvertLeadForm(leadService.findById(leadId).get()));
        return "deals/convert";
    }

    @PostMapping("/convert")
    public String convertLeadToDeal(@RequestParam UUID leadId, @RequestParam BigDecimal amount) {
        dealService.convertLeadToDeal(leadId, amount);
        return "redirect:/deals";
    }

    @GetMapping("/{id}/transitions")
    @ResponseBody
    public Set<DealStatus> getAvailableTransitions(@PathVariable UUID id) {
        Deal deal = dealService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сделка не найдена"));
        return deal.getStatus().getValidTransitions();
    }

    @PostMapping("/{id}/transition")
    public String transitionStatus(@PathVariable UUID id, @RequestParam DealStatus newStatus) {
        dealService.transitionDealStatus(id, newStatus);
        return "redirect:/deals";
    }
}