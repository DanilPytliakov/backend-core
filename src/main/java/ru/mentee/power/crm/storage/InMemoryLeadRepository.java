package ru.mentee.power.crm.storage;

import java.util.*;

import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.Repository;

public class InMemoryLeadRepository implements Repository<Lead> {
    private final ArrayList<Lead> storage = new ArrayList<Lead>();

    // Так как прямого доступа к ArrayList мы иметь не должны, то этот метод понадобиться
    public int getStorageSize() {
        return storage.size();
    }

    @Override
    public void add(Lead entity) {
        if (!storage.contains(entity)) {
            storage.add(entity);
        }
    }

    @Override
    public void remove(UUID targetId) {
        for (int i = 0; i < storage.size(); i++) {
            if (Objects.equals(storage.get(i).id(), targetId)) {
                storage.remove(i);
                break;
            }
        }
    }

    @Override
    public Optional<Lead> findById(UUID targetId) {
        return storage.stream()
                .filter(lead -> Objects.equals(lead.id(), targetId)).
                findFirst();
    }

    @Override
    public List<Lead> findAll() {
        List<Lead> listForReturn = new ArrayList<Lead>(storage);
        return listForReturn;
    }
}