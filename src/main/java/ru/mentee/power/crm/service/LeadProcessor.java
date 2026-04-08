package ru.mentee.power.crm.service;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
@AllArgsConstructor
public class LeadProcessor {

  private final LeadRepository leadRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void processSingleLead(UUID id) {
    Lead lead =
        leadRepository
            .findById(id)
            .orElseThrow(() -> new IllegalStateException("Лид не найден: " + id));
    lead.setStatus(LeadStatus.CONTACTED);
    leadRepository.save(lead);
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void processSingleLeadMandatory(UUID id) {
    Lead lead =
        leadRepository
            .findById(id)
            .orElseThrow(() -> new IllegalStateException("Лид не найден: " + id));
    lead.setStatus(LeadStatus.CONTACTED);
    leadRepository.save(lead);
  }
}
