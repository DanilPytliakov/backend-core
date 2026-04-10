package ru.mentee.power.crm.service;

import static ru.mentee.power.crm.repository.LeadSpecifications.*;

import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.dto.CreateLeadForm;
import ru.mentee.power.crm.dto.UpdateLeadForm;
import ru.mentee.power.crm.repository.CompanyRepository;
import ru.mentee.power.crm.repository.DealRepository;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.spring.client.EmailValidationFeignClient;
import ru.mentee.power.crm.spring.client.EmailValidationResponse;

@Service
@RequiredArgsConstructor
public class LeadService {

  private final LeadRepository leadRepository;
  private final DealRepository dealRepository;
  private final LeadProcessor leadProcessor;
  private final CompanyRepository companyRepository;
  private final EmailValidationFeignClient emailValidationClient;

  /**
   * Прокси-самоссылка: вызовы через {@code self} проходят через Spring AOP, иначе {@code @Retry} не
   * сработает.
   */
  private LeadService self;

  private static final Logger LOG = LoggerFactory.getLogger(LeadService.class);

  @Autowired
  @Lazy
  public void setSelf(LeadService self) {
    this.self = self;
  }

  public Optional<Lead> addLead(String name, String email, Company company, LeadStatus status) {
    self.validateEmailOrThrow(email);
    Optional<Lead> existing = leadRepository.findByEmail(email);

    if (existing.isPresent()) {
      return Optional.empty();
    } else {
      Lead lead = new Lead(name, email, company, status);
      return Optional.of(leadRepository.save(lead));
    }
  }

  public Optional<Lead> addLead(String name, String email, Company company) {
    return addLead(name, email, company, LeadStatus.NEW);
  }

  public Optional<Lead> addLead(Lead lead) {
    return addLead(lead.getName(), lead.getEmail(), lead.getCompany(), lead.getStatus());
  }

  public Optional<Lead> addLead(CreateLeadForm form) {
    self.validateEmailOrThrow(form.getEmail());
    if (leadRepository.findByEmail(form.getEmail()).isPresent()) {
      return Optional.empty(); // явно говорим "лид не создан"
    }

    return companyRepository
        .findById(form.getCompanyId())
        .map(company -> new Lead(form.getName(), form.getEmail(), company))
        .map(leadRepository::save);
  }

  @Retry(name = "email-validation", fallbackMethod = "validateEmailFallback")
  public void validateEmailOrThrow(String email) {
    EmailValidationResponse validation = emailValidationClient.validateEmail(email);
    if (!validation.valid()) {
      throw new IllegalArgumentException("Invalid email: " + validation.reason());
    }
  }

  public void validateEmailFallback(String email, Exception ex) {
    LOG.warn(
        "Email validation service unavailable after retries. Skipping validation for email={}. Error: {}",
        email,
        ex.getMessage());
  }

  @PostConstruct
  void init() {
    LOG.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
  }

  public List<Lead> findAll() {
    return new ArrayList<Lead>(leadRepository.findAll());
  }

  public Optional<Lead> findById(UUID id) {
    return leadRepository.findById(id);
  }

  public List<Lead> findByFilter(
      String name, String email, String companyName, String companyIndustry, LeadStatus status) {

    Specification<Lead> spec =
        hasName(name)
            .and(hasEmail(email))
            .and(hasCompanyName(companyName))
            .and(hasCompanyIndustry(companyIndustry))
            .and(hasStatus(status));

    return leadRepository.findAll(spec);
  }

  public Optional<Lead> updateLead(
      UUID id, String name, String email, Company company, LeadStatus status) {
    return leadRepository
        .findById(id)
        .map(
            lead -> {
              lead.setName(name);
              lead.setEmail(email);
              lead.setCompany(company);
              lead.setStatus(status);
              return leadRepository.save(lead);
            });
  }

  public Optional<Lead> updateLead(Lead lead) {
    return Optional.ofNullable(lead)
        .map(Lead::getId)
        .flatMap(leadRepository::findById)
        .map(
            existingLead -> {
              existingLead.setName(lead.getName());
              existingLead.setEmail(lead.getEmail());
              existingLead.setCompany(lead.getCompany());
              existingLead.setStatus(lead.getStatus());
              return leadRepository.save(existingLead);
            });
  }

  public Optional<Lead> updateLead(UpdateLeadForm form) {
    if (form == null) {
      return Optional.empty();
    }

    Optional<Company> company =
        Optional.ofNullable(form.getCompanyId()).flatMap(companyRepository::findById);

    return updateLead(
        form.getId(), form.getName(), form.getEmail(), company.orElse(null), form.getStatus());
  }

  public boolean deleteLead(UUID id) {
    if (leadRepository.findById(id).isEmpty()) {
      return false;
    }
    leadRepository.deleteById(id);
    return true;
  }

  // Поиск лида по email (derived method).
  public Optional<Lead> findByEmail(String email) {
    return leadRepository.findByEmail(email);
  }

  // Поиск лидов по списку статусов (JPQL).
  public List<Lead> findByStatuses(LeadStatus... statuses) {
    return leadRepository.findByStatusIn(List.of(statuses));
  }

  // Получить первую страницу лидов с сортировкой.
  public Page<Lead> getFirstPage(int pageSize) {
    PageRequest pageRequest =
        PageRequest.of(
            0, // первая страница (нумерация с 0)
            pageSize,
            Sort.by("createdAt").descending());
    return leadRepository.findAll(pageRequest);
  }

  @Transactional
  public int convertNewToContacted() {
    int updated = leadRepository.updateStatusBulk(LeadStatus.NEW, LeadStatus.CONTACTED);
    // Логируем для observability
    System.out.printf("Converted %d leads from NEW to CONTACTED%n", updated);
    return updated;
  }

  @Transactional
  public int archiveOldLeads(LeadStatus status) {
    return leadRepository.deleteByStatusBulk(status);
  }

  @Transactional
  public void convertLeadToDeal(UUID leadId, BigDecimal amount) {
    Lead lead =
        leadRepository
            .findById(leadId)
            .orElseThrow(() -> new IllegalStateException("Лид не найден: " + leadId));
    dealRepository.save(new Deal(leadId, amount));
    lead.setStatus(LeadStatus.CONTACTED);
    leadRepository.save(lead);
  }

  @Transactional
  public void processLeads(List<UUID> ids) {
    for (UUID id : ids) {
      leadProcessor.processSingleLead(id);
    }
  }

  public List<Lead> findByCompanyId(UUID companyId) {
    return leadRepository.findByCompanyId(companyId);
  }
}
