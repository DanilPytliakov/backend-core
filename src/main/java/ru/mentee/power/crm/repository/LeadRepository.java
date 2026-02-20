package ru.mentee.power.crm.repository;

import java.util.*;

import ru.mentee.power.crm.model.Lead;

public interface LeadRepository {

    Lead save(Lead lead);

    Optional<Lead> findById(UUID id);

    Optional<Lead> findByEmail(String email);

    List<Lead> findAll();

    void delete(UUID id);
}