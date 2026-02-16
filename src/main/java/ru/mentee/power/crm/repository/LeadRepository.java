package ru.mentee.power.crm.repository;

import java.util.*;

import ru.mentee.power.crm.model.Lead;

public class LeadRepository {
    private final Map<String, Lead> storage = new HashMap<>();

    public void save(Lead lead) {
        storage.put(lead.id(), lead);
    }

    public Lead findById(String id) {
        return storage.get(id);
    }

    public List<Lead> findAll() {
        // TODO: Вернуть все значения из storage через values()
        // Преобразовать Collection в List
        return new ArrayList<>(storage.values());
    }

    public void delete(String id) {
        storage.remove(id);
        // TODO: Удалить лид через storage.remove(id)
    }

    public int size() {
        return storage.size();
    }
}