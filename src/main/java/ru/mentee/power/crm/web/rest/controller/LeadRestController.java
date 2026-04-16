package ru.mentee.power.crm.web.rest.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.service.CompanyService;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.spring.dto.generated.UpdateLeadRequest;
import ru.mentee.power.crm.spring.rest.generated.LeadManagementApi;
import ru.mentee.power.crm.web.rest.mapper.LeadMapper;

@RestController
@RequiredArgsConstructor
public class LeadRestController implements LeadManagementApi {

  private final LeadService leadService;
  private final CompanyService companyService;
  private final LeadMapper leadMapper;

  // Возвращает полный список лидов.
  // 200 OK + JSON-массив (включая пустой массив, если записей нет).
  @Override
  public ResponseEntity<List<LeadResponse>> getAllLeads() {
    List<LeadResponse> responses =
        leadService.findAll().stream().map(leadMapper::toResponse).toList();
    return ResponseEntity.ok(responses);
  }

  // Возвращает лид по id.
  // 200 OK + JSON лида, если найден.
  // 404 Not Found, если лида с таким id нет.
  @Override
  public ResponseEntity<LeadResponse> getLeadById(UUID id) {
    return ResponseEntity.ok(leadMapper.toResponse(leadService.getLeadOrThrow(id)));
  }

  // Создает нового лида.
  // 201 Created + Location: /api/leads/{id} + JSON созданного лида при успехе.
  // 409 Conflict, если создать нельзя (например, лид с таким email уже существует).
  @Override
  public ResponseEntity<LeadResponse> createLead(CreateLeadRequest request) {
    Lead lead = leadMapper.toEntity(request);
    Company company = findCompanyOrNull(request.getCompanyId());
    lead.setCompany(company);

    return leadService
        .addLead(lead.getName(), lead.getEmail(), lead.getCompany(), lead.getStatus())
        .map(
            createdLead ->
                ResponseEntity.created(URI.create("/api/leads/" + createdLead.getId()))
                    .body(leadMapper.toResponse(createdLead)))
        .orElse(ResponseEntity.status(409).build());
  }

  @Override
  public ResponseEntity<LeadResponse> updateLead(UUID id, UpdateLeadRequest request) {
    Lead existingLead = leadService.getLeadOrThrow(id);
    leadMapper.updateEntity(request, existingLead);
    Company company = findCompanyOrNull(request.getCompanyId());
    existingLead.setCompany(company);
    Lead updatedLead = leadService.updateLeadOrThrow(existingLead);
    return ResponseEntity.ok(leadMapper.toResponse(updatedLead));
  }

  @Override
  public ResponseEntity<Void> deleteLead(UUID id) {
    leadService.deleteLeadOrThrow(id);
    return ResponseEntity.noContent().build();
  }

  private Company findCompanyOrNull(JsonNullable<UUID> companyId) {
    if (companyId == null || !companyId.isPresent()) {
      return null;
    }
    return companyService.findById(companyId.get()).orElse(null);
  }
}
