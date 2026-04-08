package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.CompanyRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@SpringBootTest
@Transactional
class LeadServiceTest {

  @Autowired private LeadService service;
  @Autowired private LeadRepository repository;
  @Autowired private CompanyRepository companyRepository;
  @Autowired private LeadService leadService;

  Company firstCompany;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
    firstCompany = companyRepository.save(new Company("FirstCompany", "buisines"));
    // Создаём 3 NEW лида
    for (int i = 1; i <= 3; i++) {
      Lead lead = new Lead("Lead" + i, "lead" + i + "@example.com", firstCompany);
      repository.save(lead);
    }
  }

  @Test
  void convertNewToContacted_shouldUpdateMultipleLeads() {
    // When
    int updated = service.convertNewToContacted();

    // Then
    assertThat(updated).isEqualTo(3);

    // Проверяем что статус изменился
    long contactedCount = repository.countByStatus(LeadStatus.CONTACTED);
    assertThat(contactedCount).isEqualTo(3);

    long newCount = repository.countByStatus(LeadStatus.NEW);
    assertThat(newCount).isEqualTo(0);
  }

  @Test
  void archiveOldLeads_ShouldArchiveMultipleLeads() {
    // When
    assertThat(repository.countByStatus(LeadStatus.NEW)).isEqualTo(3);

    // Then
    service.archiveOldLeads(LeadStatus.NEW);

    // Проверяем что все лиды со статусом NEW заархивированы
    assertThat(repository.countByStatus(LeadStatus.NEW)).isZero();
  }
}
