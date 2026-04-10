package ru.mentee.power.crm.spring.rest;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.service.LeadService;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadRestController {

  private final LeadService leadService;

  // Возвращает полный список лидов.
  // 200 OK + JSON-массив (включая пустой массив, если записей нет).
  @GetMapping
  public ResponseEntity<List<Lead>> getAllLeads() {
    List<Lead> leads = leadService.findAll();
    return ResponseEntity.ok(leads);
  }

  // Возвращает лид по id.
  // 200 OK + JSON лида, если найден.
  // 404 Not Found, если лида с таким id нет.
  @GetMapping("/{id}")
  public ResponseEntity<Lead> getLeadById(@PathVariable UUID id) {
    Optional<Lead> lead = leadService.findById(id);
    return lead.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  // Создает нового лида.
  // 201 Created + Location: /api/leads/{id} + JSON созданного лида при успехе.
  // 409 Conflict, если создать нельзя (например, лид с таким email уже существует).
  @PostMapping
  public ResponseEntity<Lead> createLead(@RequestBody Lead lead) {
    return leadService
        .addLead(lead)
        .map(
            createdLead ->
                ResponseEntity.created(URI.create("/api/leads/" + createdLead.getId()))
                    .body(createdLead))
        .orElse(ResponseEntity.status(409).build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Lead> updateLead(@PathVariable UUID id, @RequestBody Lead lead) {
    return leadService
        .updateLead(id, lead.getName(), lead.getEmail(), lead.getCompany(), lead.getStatus())
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLead(@PathVariable UUID id) {
    if (leadService.deleteLead(id)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
