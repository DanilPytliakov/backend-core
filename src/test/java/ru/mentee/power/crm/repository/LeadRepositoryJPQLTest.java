package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

@DataJpaTest
class LeadRepositoryJPQLTest {

    @Autowired
    private LeadRepository repository;

    private Lead lead1;
    private Lead lead2;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        lead1 = new Lead();
        lead1.setName("Lead 1");
        lead1.setEmail("john@example.com");
        lead1.setCompany("ACME Corp");
        lead1.setStatus(LeadStatus.NEW);
        lead1.setCreatedAt(LocalDateTime.now().minusDays(5));
        repository.save(lead1);

        lead2 = new Lead();
        lead2.setName("Lead 2");
        lead2.setEmail("jane@example.com");
        lead2.setCompany("Tech Inc");
        lead2.setStatus(LeadStatus.CONTACTED);
        lead2.setCreatedAt(LocalDateTime.now().minusDays(2));
        repository.save(lead2);
    }

    @Test
    void findByEmail_shouldReturnLead_whenExists() {
        // When
        Optional<Lead> found = repository.findByEmail("john@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCompany()).isEqualTo("ACME Corp");
    }

    @Test
    void findByStatus_shouldReturnFilteredLeads() {
        // When
        List<Lead> newLeads = repository.findByStatus(LeadStatus.NEW);

        // Then
        assertThat(newLeads).hasSize(1);
        assertThat(newLeads.get(0).getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findByStatusIn_shouldReturnLeadsWithMultipleStatuses() {
        // Given
        List<LeadStatus> statuses = List.of(LeadStatus.NEW, LeadStatus.CONTACTED);

        // When
        List<Lead> found = repository.findByStatusIn(statuses);

        // Then
        assertThat(found).hasSize(2);
    }

    @Test
    void findAll_withPageable_shouldReturnPage() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 1);

        // When
        Page<Lead> page = repository.findAll(pageRequest);

        // Then
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(0); // текущая страница
    }

    @Test
    void countAllLeadsWithCurrentStatus() {
        assertThat(repository.countByStatus(LeadStatus.NEW)).isEqualTo(1);
        assertThat(repository.countByStatus(LeadStatus.CONTACTED)).isEqualTo(1);
        assertThat(repository.countByStatus(LeadStatus.QUALIFIED)).isZero();
    }

    @Test
    void shouldShowIFLeads_WithCurrentEmail_Exist() {
        assertThat(repository.existsByEmail("john@example.com")).isTrue();
        assertThat(repository.existsByEmail("jane@example.com")).isTrue();
        assertThat(repository.existsByEmail("unvalidEmail@example.com")).isFalse();

    }

    @Test
    void shouldShowLeads_WithCurrentEmail_AndCompany() {
        // Валидные статус и название фирмы
        assertThat(
                repository.findByStatusAndCompany(
                        LeadStatus.NEW,
                        "ACME Corp")).
                hasSize(1);

        // Валидное название фирмы, но не статус
        assertThat(
                repository.findByStatusAndCompany(
                        LeadStatus.QUALIFIED,
                        "ACME Corp")).
                isEmpty();

        // Валидный статус, но не название фирмы
        assertThat(
                repository.findByStatusAndCompany(
                        LeadStatus.NEW,
                        "Invalid Corp")).
                isEmpty();
    }

    @Test
    @Transactional
    void updateStatusBulk_shouldUpdateAllLeadsWithOldStatus() {
        // Given — уже есть lead1 (NEW) и lead2 (CONTACTED) из setUp

        // When
        int updated = repository.updateStatusBulk(LeadStatus.NEW, LeadStatus.QUALIFIED);

        // Then
        assertThat(updated).isEqualTo(1); // только lead1 был NEW

        Optional<Lead> updatedLead = repository.findByEmail("john@example.com");
        assertThat(updatedLead.get().getStatus()).isEqualTo(LeadStatus.QUALIFIED);

        // lead2 остался CONTACTED
        Optional<Lead> untouchedLead = repository.findByEmail("jane@example.com");
        assertThat(untouchedLead.get().getStatus()).isEqualTo(LeadStatus.CONTACTED);
    }
}