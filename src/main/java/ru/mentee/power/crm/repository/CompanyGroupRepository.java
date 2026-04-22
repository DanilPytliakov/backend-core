package ru.mentee.power.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.CompanyGroup;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyGroupRepository extends JpaRepository<CompanyGroup, UUID> {

    // Поиск по email (уникальное поле)
    Optional<CompanyGroup> findByEmail(String email);

    // Проверка существования по email
    boolean existsByEmail(String email);
}