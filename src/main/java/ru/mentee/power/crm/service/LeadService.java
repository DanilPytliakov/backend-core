package ru.mentee.power.crm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.repository.RepositoryInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LeadService {

    private final RepositoryInterface repositoryInterface;
    private static final Logger log = LoggerFactory.getLogger(LeadService.class);

    public LeadService(LeadRepository repository) {
        this.repositoryInterface = repository;
        log.info("LeadService constructor called");
    }

    public Lead addLead(String email, String company, LeadStatus status) {
        // Бизнес-правило: проверка уникальности email
        Optional<Lead> existing = repositoryInterface.findByEmail(email);
        if (existing.isPresent()) {
            throw new IllegalStateException("Lead with email already exists: " + email);
        }

        // Создаём нового лида
        Lead lead = new Lead(
                UUID.randomUUID(),
                email,
                company,
                status
        );

        // Сохраняем через repository
        return repositoryInterface.save(lead);
    }

    @PostConstruct
    void init() {
        log.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
    }

    public List<Lead> findAll() {
        return new ArrayList<Lead>(repositoryInterface.findAll());
    }

    public Optional<Lead> findById(UUID id) {
        return repositoryInterface.findById(id);
    }

    public Optional<Lead> findByEmail(String email) {
        return repositoryInterface.findByEmail(email);
    }

    public List<Lead> findByStatus(LeadStatus status) {
        return repositoryInterface.findAll().stream()
                .filter(lead -> lead.status().equals(status))
                .collect(Collectors.toList());
    }

}