package ru.mentee.power.crm.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;

@Repository
public interface DealRepository extends JpaRepository<Deal, UUID> {

    List<Deal> findByStatus(DealStatus status);

    List<Deal> findByLeadId(UUID leadId);

    long countByStatus(DealStatus status);
}