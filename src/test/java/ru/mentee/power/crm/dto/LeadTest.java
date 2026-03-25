package ru.mentee.power.crm.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

class LeadTest {

    @Test
    void shouldCreateContact_whenValidData() {
        // When
        Lead lead = new Lead("Danil", "example@gmail.com", "TechCorp");

        // Then
        assertThat(lead.getName()).isEqualTo("Danil");
        assertThat(lead.getEmail()).isEqualTo("example@gmail.com");
        assertThat(lead.getCompany()).isEqualTo("TechCorp");
        assertThat(lead.getStatus()).isEqualTo(LeadStatus.NEW);
    }

    @Test
    void shouldBeEqual_whenSameData() {
        // When
        UUID id = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.now();
        Lead firstLead = new Lead("Danil", "example@gmail.com", "TechCorp");
        Lead secondLead = new Lead("Danil", "example@gmail.com", "TechCorp");

        //Then
        assertThat(firstLead.equals(secondLead)).isTrue();
        assertThat(firstLead.hashCode()).isEqualTo(secondLead.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenDifferentData() {
        // Создаём два лида с разными данными
        Lead lead1 = new Lead("Alice", "alice@example.com", "CompanyA");
        Lead lead2 = new Lead("Bob", "bob@example.com", "CompanyB");

        // Проверяем, что ключевые поля различаются
        assertThat(lead1.getName()).isNotEqualTo(lead2.getName());
        assertThat(lead1.getEmail()).isNotEqualTo(lead2.getEmail());
        assertThat(lead1.getCompany()).isNotEqualTo(lead2.getCompany());

        // Дополнительно, если хочешь, можно проверить, что объекты не равны по ссылке
        assertThat(lead1).isNotSameAs(lead2);
    }
}