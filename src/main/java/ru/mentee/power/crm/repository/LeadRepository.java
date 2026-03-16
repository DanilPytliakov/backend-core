package ru.mentee.power.crm.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Lead;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID> {
    /*
    Optional<Lead> findByEmail(String email);
    List<Lead> findByStatus(String status);
     */

    @Query(value = "SELECT * FROM leads WHERE email = ?1", nativeQuery = true)
    Optional<Lead> findByEmail(String email);

    @Query(value = "SELECT * FROM leads WHERE status = ?1", nativeQuery = true)
    List<Lead> findByStatus(String status);
}