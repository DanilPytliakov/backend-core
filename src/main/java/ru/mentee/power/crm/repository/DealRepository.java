package ru.mentee.power.crm.repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;

@Repository
public class DealRepository implements DealRepositoryInterface {
    private final Map<UUID, Deal> storage = new ConcurrentHashMap<>();

    @Override
    public Deal save(Deal deal) {
        Objects.requireNonNull(deal, "Deal не может быть null");
        storage.put(deal.getId(), deal);
        return deal;
    }

    @Override
    public Optional<Deal> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Deal> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Deal> findByStatus(DealStatus status) {
        return storage.values().stream()
                .filter(deal -> deal.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }
}