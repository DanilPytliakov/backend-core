package ru.mentee.power.crm.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Invitee;
import ru.mentee.power.crm.exception.EntityNotFoundException;
import ru.mentee.power.crm.repository.InviteeRepository;
import ru.mentee.power.crm.web.rest.dto.CreateInviteeRequest;
import ru.mentee.power.crm.web.rest.dto.InviteeResponse;
import ru.mentee.power.crm.web.rest.dto.UpdateInviteeStatusRequest;
import ru.mentee.power.crm.web.rest.mapper.InviteeMapper;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InviteeService {

  private final InviteeRepository repository;
  private final InviteeMapper mapper;

  @Transactional
  public InviteeResponse create(CreateInviteeRequest request) {
    Invitee invitee = mapper.toEntity(request);
    invitee.setStatus("NEW");
    Invitee saved = repository.save(invitee);
    log.info("Created invitee with id: {}", saved.getId());

    return mapper.toResponse(saved);
  }

  public InviteeResponse getById(UUID id) {
    Invitee invitee =
        repository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Invitee", id.toString()));
    return mapper.toResponse(invitee);
  }

  @Transactional
  public void updateStatus(UUID id, UpdateInviteeStatusRequest request) {
    Invitee invitee =
        repository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Invitee", id.toString()));

    invitee.setStatus(request.status());
    repository.save(invitee);
    log.info("Updated status for invitee {} to {}", id, request.status());
  }

  @Transactional
  public void delete(UUID id) {
    if (!repository.existsById(id)) {
      throw new EntityNotFoundException("Invitee", id.toString());
    }
    repository.deleteById(id);
    log.info("Deleted invitee with id: {}", id);
  }

  public Page<InviteeResponse> getAll(Pageable pageable) {
    return repository.findAll(pageable).map(mapper::toResponse);
  }
}
