package ru.mentee.power.crm.web.rest.fixed;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.service.InviteeService;
import ru.mentee.power.crm.web.rest.dto.CreateInviteeRequest;
import ru.mentee.power.crm.web.rest.dto.InviteeResponse;
import ru.mentee.power.crm.web.rest.dto.UpdateInviteeStatusRequest;

@Slf4j
@RestController
@RequestMapping("/api/v1/invitees")
@RequiredArgsConstructor
public class InviteeController {

  private final InviteeService inviteeService;

  @GetMapping
  public ResponseEntity<Page<InviteeResponse>> getInvitees(
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    log.info("Fetching invitees with pageable: {}", pageable);
    return ResponseEntity.ok(inviteeService.getAll(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<InviteeResponse> getInviteeById(@PathVariable UUID id) {
    log.info("Fetching invitee by id: {}", id);
    return ResponseEntity.ok(inviteeService.getById(id));
  }

  @PostMapping
  public ResponseEntity<InviteeResponse> createInvitee(
      @Valid @RequestBody CreateInviteeRequest request) {
    log.info("Creating new invitee with email: {}", request.email());
    InviteeResponse created = inviteeService.create(request);
    URI location = URI.create("/api/v1/invitees/" + created.id());
    return ResponseEntity.created(location).body(created);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<Void> updateInviteeStatus(
      @PathVariable UUID id, @Valid @RequestBody UpdateInviteeStatusRequest request) {
    log.info("Updating status for invitee {} to {}", id, request.status());
    inviteeService.updateStatus(id, request);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteInvitee(@PathVariable UUID id) {
    log.info("Deleting invitee with id: {}", id);
    inviteeService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
