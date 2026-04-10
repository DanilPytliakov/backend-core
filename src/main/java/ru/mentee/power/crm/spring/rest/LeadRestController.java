package ru.mentee.power.crm.spring.rest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.service.LeadService;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadRestController {

  private final LeadService leadService;

  @GetMapping
  public List<Lead> getAllLeads() {
    return leadService.findAll();
  }

  @GetMapping("/{id}")
  public Lead getLeadById(@PathVariable UUID id) {
    Optional<Lead> lead = leadService.findById(id);
    return lead.orElse(null);
  }

  @PostMapping
  public Lead createLead(@RequestBody Lead lead) {
    Optional<Lead> result = leadService.addLead(lead);
    return result.orElse(null);
  }
}
