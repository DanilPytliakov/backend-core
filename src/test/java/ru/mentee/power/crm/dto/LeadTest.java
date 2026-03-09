package ru.mentee.power.crm.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

class LeadTest {

    @Test
    void shouldCreateContact_whenValidData() {
        // When
        UUID id = UUID.randomUUID();
        Lead lead = new Lead(id, "Danil", "example@gmail.com", "TechCorp", LeadStatus.NEW);

        // Then
        assertThat(lead.id()).isEqualTo(id);
        assertThat(lead.name()).isEqualTo("Danil");
        assertThat(lead.email()).isEqualTo("example@gmail.com");
        assertThat(lead.company()).isEqualTo("TechCorp");
        assertThat(lead.status()).isEqualTo(LeadStatus.NEW);
    }

    @Test
    void shouldBeEqual_whenSameData() {
        // When
        UUID id = UUID.randomUUID();
        Lead firstLead = new Lead(id, "Danil", "example@gmail.com", "TechCorp", LeadStatus.NEW);
        Lead secondLead = new Lead(id, "Danil", "example@gmail.com", "TechCorp", LeadStatus.NEW);

        //Then
        assertThat(firstLead.equals(secondLead)).isTrue();
        assertThat(firstLead.hashCode()).isEqualTo(secondLead.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenDifferentData() {
        // When
        Lead firstLead = new Lead("Danil", "example@gmail.com", "TechCorp", LeadStatus.NEW);
        Lead secondLead = new Lead("Danil", "example@gmail.com", "AnotherCorp", LeadStatus.CONTACTED);

        // Then
        assertThat(firstLead.equals(secondLead)).isFalse();
    }
}