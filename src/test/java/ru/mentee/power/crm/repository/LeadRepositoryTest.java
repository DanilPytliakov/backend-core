package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.mentee.power.crm.domain.Lead;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LeadRepositoryTest {

    @Autowired
    private LeadRepository repository;

    @Test
    void shouldSaveAndFindLeadById_whenValidData() {
        // Given
        Lead lead = new Lead("Danil", "test@example.com", "NEW");

        // When
        Lead saved = repository.save(lead);
        Optional<Lead> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldFindByEmailNative_whenLeadExists() {
        // Given
        Lead lead = new Lead("Danil", "test@example.com", "TechCorp");
        repository.save(lead);

        // When
        Optional<Lead> found = repository.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getCompany()).isEqualTo("TechCorp");
    }

    @Test
    void shouldReturnEmptyOptional_whenEmailNotFound() {
        // When
        Optional<Lead> found = repository.findByEmail("nonexistent@test.com");

        // Then
        assertThat(found.isEmpty()).isTrue();
    }

    @Test
    void shouldReturnLeadsAmount_IfTheyExist() {
        assertThat(repository.findAll()).isEmpty();

        repository.save(new Lead("Danil", "test@example.com", "TechCorp"));

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void leadsMustBeDeletableBYid() {
        // Given
        Lead lead = new Lead("Danil", "test@example.com", "TechCorp");
        repository.save(lead);
        assertThat(repository.findAll()).hasSize(1);

        // When
        repository.deleteById(lead.getId());

        // Then
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void leadsMustBeDeletable() {
        // Given
        Lead lead = new Lead("Danil", "test@example.com", "TechCorp");
        repository.save(lead);
        assertThat(repository.findAll()).hasSize(1);

        // When
        repository.delete(lead);

        // Then
        assertThat(repository.findAll()).isEmpty();
    }
}