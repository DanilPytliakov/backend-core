package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LeadRepositoryTest {

  @Autowired private LeadRepository repository;
  @Autowired private CompanyRepository companyRepository;
  private Company company;

  @BeforeEach
  void setUp() {
    company = companyRepository.save(new Company("FirstCompany", "buisines"));
  }

  @Test
  void shouldSaveAndFindLeadById_whenValidData() {
    // Given
    Lead lead = new Lead("Danil", "test@example.com", company);

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
    Lead lead = new Lead("Danil", "test@example.com", company);
    repository.save(lead);

    // When
    Optional<Lead> found = repository.findByEmail("test@example.com");

    assertThat(found).isPresent();
    assertThat(found.get().getCompany()).isEqualTo(company);
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

    repository.save(new Lead("Danil", "test@example.com", company));

    assertThat(repository.findAll()).hasSize(1);
  }

  @Test
  void leadsMustBeDeletableBYid() {
    // Given
    Lead lead = new Lead("Danil", "test@example.com", company);
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
    Lead lead = new Lead("Danil", "test@example.com", company);
    repository.save(lead);
    assertThat(repository.findAll()).hasSize(1);

    // When
    repository.delete(lead);

    // Then
    assertThat(repository.findAll()).isEmpty();
  }
}
