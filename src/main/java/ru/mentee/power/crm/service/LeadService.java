package ru.mentee.power.crm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.repository.RepositoryInterface;

@Service
public class LeadService {

    private final RepositoryInterface repositoryInterface;
    private static final Logger LOG = LoggerFactory.getLogger(LeadService.class);

    public LeadService(LeadRepository repository) {
        this.repositoryInterface = repository;
        LOG.info("LeadService constructor called");
    }

    public Optional<Lead> addLead(String email, String company, LeadStatus status) {
        Optional<Lead> existing = repositoryInterface.findByEmail(email);

        if (existing.isPresent()) {
            return Optional.empty(); // явно говорим "лид не создан"
        } else {
            Lead lead = new Lead(UUID.randomUUID(), email, company, status);
            return Optional.of(repositoryInterface.save(lead));
        }
    }

    @PostConstruct
    void init() {
        LOG.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
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

    public List<Lead> findByFilter(String email, String company, LeadStatus status) {
        return repositoryInterface.findAll().stream()
                .filter(lead -> email == null || email.isBlank() || lead.email().contains(email))
                .filter(lead -> company == null || company.isBlank() || lead.company().contains(company))
                .filter(lead -> status == null || lead.status().equals(status))
                .collect(Collectors.toList());
    }

    public void updateLead(UUID id, String email, String company, LeadStatus status) {
        Optional<Lead> existing = repositoryInterface.findById(id);
        if (existing.isPresent()) {
            Lead updated = new Lead(id, email, company, status);
            repositoryInterface.save(updated);
        }
    }

    public  void deleteLead(UUID id) {
        if (repositoryInterface.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repositoryInterface.delete(id);
    }

}