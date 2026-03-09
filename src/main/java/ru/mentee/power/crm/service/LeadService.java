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
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.dto.CreateLeadForm;
import ru.mentee.power.crm.repository.LeadRepositoryInterface;

@Service
public class LeadService {

    private final LeadRepositoryInterface leadRepositoryInterface;
    private static final Logger LOG = LoggerFactory.getLogger(LeadService.class);

    public LeadService(LeadRepositoryInterface repository) {
        this.leadRepositoryInterface = repository;
        LOG.info("LeadService constructor called");
    }

    public Optional<Lead> addLead(String name, String email, String company, LeadStatus status) {
        Optional<Lead> existing = leadRepositoryInterface.findByEmail(email);

        if (existing.isPresent()) {
            return Optional.empty(); // явно говорим "лид не создан"
        } else {
            Lead lead = new Lead(name, email, company, status);
            return Optional.of(leadRepositoryInterface.save(lead));
        }
    }

    public Optional<Lead> addLead(CreateLeadForm form) {
        Optional<Lead> existing = leadRepositoryInterface.findByEmail(form.getEmail());

        if (existing.isPresent()) {
            return Optional.empty(); // явно говорим "лид не создан"
        } else {
            Lead lead = new Lead(form.getName(), form.getEmail(), form.getCompany(), form.getStatus());
            return Optional.of(leadRepositoryInterface.save(lead));
        }
    }

    @PostConstruct
    void init() {
        LOG.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
    }

    public List<Lead> findAll() {
        return new ArrayList<Lead>(leadRepositoryInterface.findAll());
    }

    public Optional<Lead> findById(UUID id) {
        return leadRepositoryInterface.findById(id);
    }

    public Optional<Lead> findByEmail(String email) {
        return leadRepositoryInterface.findByEmail(email);
    }

    public List<Lead> findByFilter(String name, String email, String company, LeadStatus status) {
        return leadRepositoryInterface.findAll().stream()
                .filter(lead -> name == null
                        || name.isBlank()
                        || lead.name().toLowerCase().contains(name.toLowerCase()))
                .filter(lead -> email == null
                        || email.isBlank()
                        || lead.email().toLowerCase().contains(email.toLowerCase()))
                .filter(lead -> company == null
                        || company.isBlank() 
                        || lead.company().toLowerCase().contains(company.toLowerCase()))
                .filter(lead -> status == null || lead.status().equals(status))
                .collect(Collectors.toList());
    }

    public void updateLead(UUID id, String name, String email, String company, LeadStatus status) {
        Optional<Lead> existing = leadRepositoryInterface.findById(id);
        if (existing.isPresent()) {
            Lead updated = new Lead(id, name, email, company, status);
            leadRepositoryInterface.save(updated);
        }
    }

    public  void deleteLead(UUID id) {
        if (leadRepositoryInterface.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        leadRepositoryInterface.delete(id);
    }

}