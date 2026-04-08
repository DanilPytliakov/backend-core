package ru.mentee.power.crm.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

class LeadTest {

  private Company firstCompany = new Company("Первая", "бизнес");
  private Company secondCompany = new Company("Вторая", "бизнес");

  @Test
  void shouldCreateContact_whenValidData() {
    // When
    Lead lead = new Lead("Danil", "example@gmail.com", firstCompany);

    // Then
    assertThat(lead.getName()).isEqualTo("Danil");
    assertThat(lead.getEmail()).isEqualTo("example@gmail.com");
    assertThat(lead.getCompany()).isEqualTo(firstCompany);
    assertThat(lead.getStatus()).isEqualTo(LeadStatus.NEW);
  }

  @Test
  void shouldBeEqual_whenSameData() {
    // When
    UUID id = UUID.randomUUID();
    LocalDateTime date = LocalDateTime.now();
    Lead firstLead = new Lead("Danil", "example@gmail.com", firstCompany);
    Lead secondLead = new Lead("Danil", "example@gmail.com", firstCompany);

    // Then
    assertThat(firstLead.equals(secondLead)).isTrue();
    assertThat(firstLead.hashCode()).isEqualTo(secondLead.hashCode());
  }

  @Test
  void shouldNotBeEqual_whenDifferentData() {
    // Создаём два лида с разными данными
    Lead lead1 = new Lead("Alice", "alice@example.com", firstCompany);
    Lead lead2 = new Lead("Bob", "bob@example.com", secondCompany);

    // Проверяем, что ключевые поля различаются
    assertThat(lead1.getName()).isNotEqualTo(lead2.getName());
    assertThat(lead1.getEmail()).isNotEqualTo(lead2.getEmail());
    assertThat(lead1.getCompany()).isNotEqualTo(lead2.getCompany());

    assertThat(lead1).isNotSameAs(lead2);
  }
}
