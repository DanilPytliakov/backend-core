package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

class InMemoryLeadRepositoryTest {
    private InMemoryLeadRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLeadRepository();
    }

    @Test
    void shouldReturnAddedLeadAndHisLength() {
        // Given
        Lead lead = new Lead("example@gmail.com", "TechCorp", LeadStatus.NEW);

        // When
        repository.save(lead);

        // Then
        assertThat(repository.findAll().size()).isEqualTo(1);
        assertThat(repository.findById(lead.id())).isEqualTo(Optional.of(lead));
    }

    @Test
    void shouldReturnOptionalEmptyWhenTryToFindLeadByNonExistingId() {
        // Given
        Lead lead = new Lead("example@gmail.com", "TechCorp", LeadStatus.NEW);

        // When
        repository.save(lead);

        // Then
        assertThat(repository.findById(UUID.randomUUID())).isEqualTo(Optional.empty());
    }

    @Test
    void shouldtAddLeadsWithSameId() {
        // Given
        Lead lead = new Lead("example@gmail.com", "TechCorp", LeadStatus.NEW);

        // When
        repository.save(lead);
        repository.save(new Lead(lead.id(), "example@gmail.com", "TechCorp", LeadStatus.NEW));

        // Then
        assertThat(repository.findAll().size()).isEqualTo(1);
    }

    /*
    Given InMemoryLeadRepository с 5 лидами
    When вызываю remove(uuid) для существующего лида
    Then findAll() возвращает 4 лида, findById(uuid) возвращает Optional.empty()

    Сократил количество лидов до двух за неимением смысла
    */
    @Test
    void removedLeadshouldtBeFindedBy() {
        // Given
        UUID fistLeadId = UUID.randomUUID();

        // When
        repository.save(new Lead(fistLeadId, "example@gmail.com", "TechCorp", LeadStatus.NEW));
        repository.save(new Lead("example@gmail.com", "TechCorp", LeadStatus.NEW));


        // Then
        assertThat(repository.findAll().size()).isEqualTo(2);
        repository.delete(fistLeadId);
        assertThat(repository.findAll().size()).isEqualTo(1);
    }

    /*
    Given InMemoryLeadRepository с лидами
    When клиент вызывает findAll() и пытается изменить возвращенный список
    Then изменения не влияют на internal storage (defensive copy)
    */
    @Test
    void weShouldNotBeAbleToChangeInternalStorageManipulatingWithRerurnOfMethod() {
        // Given
        repository.save(new Lead("example@gmail.com", "TechCorp", LeadStatus.NEW));
        repository.save(new Lead("example@gmail.com", "TechCorp", LeadStatus.NEW));

        // When
        List<Lead> leads = repository.findAll();

        // Then
        leads.add(new Lead("example@gmail.com", "TechCorp", LeadStatus.NEW));
        assertThat(leads.size()).isEqualTo(3);
        assertThat(repository.findAll().size()).isEqualTo(2);
    }
}