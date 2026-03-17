package ru.mentee.power.crm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.dto.CreateLeadForm;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository repository;
    private static final Logger LOG = LoggerFactory.getLogger(LeadService.class);

    public Optional<Lead> addLead(String name, String email, String company, LeadStatus status) {
        Optional<Lead> existing = repository.findByEmail(email);

        if (existing.isPresent()) {
            return Optional.empty(); // явно говорим "лид не создан"
        } else {
            Lead lead = new Lead(name, email, company,  status);
            return Optional.of(repository.save(lead));
        }
    }

    public Optional<Lead> addLead(String name, String email, String company) {
        return addLead(name, email, company, LeadStatus.NEW);
    }

    public Optional<Lead> addLead(CreateLeadForm form) {
        Optional<Lead> existing = repository.findByEmail(form.getEmail());

        if (existing.isPresent()) {
            return Optional.empty(); // явно говорим "лид не создан"
        } else {
            Lead lead = new Lead(form.getName(), form.getEmail(), form.getCompany());
            return Optional.of(repository.save(lead));
        }
    }

    @PostConstruct
    void init() {
        LOG.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
    }

    public List<Lead> findAll() {
        return new ArrayList<Lead>(repository.findAll());
    }

    public Optional<Lead> findById(UUID id) {
        return repository.findById(id);
    }

    public List<Lead> findByFilter(String name, String email, String company, LeadStatus status) {
        return repository.findAll().stream()
                .filter(lead -> name == null
                        || name.isBlank()
                        || lead.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(lead -> email == null
                        || email.isBlank()
                        || lead.getEmail().toLowerCase().contains(email.toLowerCase()))
                .filter(lead -> company == null
                        || company.isBlank() 
                        || lead.getCompany().toLowerCase().contains(company.toLowerCase()))
                .filter(lead -> status == null || lead.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public void updateLead(UUID id, String name, String email, String company, LeadStatus status) {
        Lead lead = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        lead.setName(name);
        lead.setEmail(email);
        lead.setCompany(company);
        lead.setStatus(status);
        repository.save(lead);
    }

    public void deleteLead(UUID id) {
        if (repository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }

    // Поиск лида по email (derived method).
    public Optional<Lead> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    // Поиск лидов по списку статусов (JPQL).
    public List<Lead> findByStatuses(LeadStatus... statuses) {
        return repository.findByStatusIn(List.of(statuses));
    }

    // Получить первую страницу лидов с сортировкой.
    public Page<Lead> getFirstPage(int pageSize) {
        PageRequest pageRequest = PageRequest.of(
                0, // первая страница (нумерация с 0)
                pageSize,
                Sort.by("createdAt").descending()
        );
        return repository.findAll(pageRequest);
    }

    public Page<Lead> searchByCompany(String company, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByCompany(company, pageable);
    }

    /**
     * Массовое обновление статуса (используется @Modifying метод).
     * ВАЖНО: @Transactional обязательна для @Modifying!
     */
    @Transactional
    public int convertNewToContacted() {
        int updated = repository.updateStatusBulk(LeadStatus.NEW, LeadStatus.CONTACTED);
        // Логируем для observability
        System.out.printf("Converted %d leads from NEW to CONTACTED%n", updated);
        return updated;
    }

    @Transactional
    public int archiveOldLeads(LeadStatus status) {
        return repository.deleteByStatusBulk(status);
    }
}