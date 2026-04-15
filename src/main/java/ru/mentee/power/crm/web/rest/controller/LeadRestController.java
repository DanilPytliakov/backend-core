package ru.mentee.power.crm.web.rest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.service.CompanyService;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.web.rest.dto.CreateLeadRequest;
import ru.mentee.power.crm.web.rest.dto.LeadResponse;
import ru.mentee.power.crm.web.rest.dto.UpdateLeadRequest;
import ru.mentee.power.crm.web.rest.mapper.LeadMapper;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@Validated
public class LeadRestController {

  private final LeadService leadService;
  private final CompanyService companyService;
  private final LeadMapper leadMapper;

  // Возвращает полный список лидов.
  // 200 OK + JSON-массив (включая пустой массив, если записей нет).
  @GetMapping
  public ResponseEntity<List<LeadResponse>> getAllLeads() {
    List<LeadResponse> responses =
        leadService.findAll().stream().map(leadMapper::toResponse).toList();
    return ResponseEntity.ok(responses);
  }

  // Возвращает лид по id.
  // 200 OK + JSON лида, если найден.
  // 404 Not Found, если лида с таким id нет.
  @GetMapping("/{id}")
  public ResponseEntity<LeadResponse> getLeadById(
      @PathVariable @NotNull(message = "ID лида обязателен") UUID id) {
    return leadService
        .findById(id)
        .map(leadMapper::toResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  // Создает нового лида.
  // 201 Created + Location: /api/leads/{id} + JSON созданного лида при успехе.
  // 409 Conflict, если создать нельзя (например, лид с таким email уже существует).
  @PostMapping
  public ResponseEntity<LeadResponse> createLead(@Valid @RequestBody CreateLeadRequest request) {
    Lead lead = leadMapper.toEntity(request);
    Company company =
        request.getCompanyId() != null
            ? companyService.findById(request.getCompanyId()).orElse(null)
            : null;
    lead.setCompany(company);

    return leadService
        .addLead(lead.getName(), lead.getEmail(), lead.getCompany(), lead.getStatus())
        .map(
            createdLead ->
                ResponseEntity.created(URI.create("/api/leads/" + createdLead.getId()))
                    .body(leadMapper.toResponse(createdLead)))
        .orElse(ResponseEntity.status(409).build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<LeadResponse> updateLead(
      @PathVariable UUID id, @RequestBody UpdateLeadRequest request) {
    return leadService
        .findById(id)
        .map(
            existingLead -> {
              leadMapper.updateEntity(request, existingLead);
              Company company =
                  request.getCompanyId() != null
                      ? companyService.findById(request.getCompanyId()).orElse(null)
                      : null;
              existingLead.setCompany(company);
              return existingLead;
            })
        .flatMap(leadService::updateLead)
        .map(leadMapper::toResponse)
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
