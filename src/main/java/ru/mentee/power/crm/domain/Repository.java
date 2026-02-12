package ru.mentee.power.crm.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T> {
    void add(T entity);

    void remove(UUID targetId);

    Optional<T> findById(UUID targetId);

    List<T> findAll();
}
