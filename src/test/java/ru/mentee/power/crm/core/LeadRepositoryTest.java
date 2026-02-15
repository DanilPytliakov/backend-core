package ru.mentee.power.crm.core;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

class LeadRepositoryTest {

    @Test
    @DisplayName("Should automatically deduplicate leads by id")
    void shouldDeduplicateLeadsById() {
        // Given
        LeadRepository leads = new LeadRepository();
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact leadContact = new Contact("test@example.com", "+71234567890", address);
        Lead lead = new Lead(leadContact, "TechCorp", "NEW");

        // When
        leads.add(lead);

        // Then
        assertThat(leads.add(lead)).isFalse();
        assertThat(leads.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should allow different leads with different ids")
    void shouldAllowDifferentLeads() {
        // Given
        LeadRepository leads = new LeadRepository();
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact leadContact = new Contact("test@example.com", "+71234567890", address);
        //Конструктор без указания Id сам генерирует ключ
        Lead fistLead = new Lead(leadContact, "TechCorp", "NEW");
        Lead secondLead = new Lead(leadContact, "TechCorp", "NEW");

        // When
        leads.add(fistLead);
        leads.add(secondLead);

        // Then
        assertThat(leads.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find existing lead through contains")
    void shouldFindExistingLead() {
        // Given
        LeadRepository leads = new LeadRepository();
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact leadContact = new Contact("test@example.com", "+71234567890", address);
        Lead lead = new Lead(leadContact, "TechCorp", "NEW");

        // When
        leads.add(lead);

        // Then
        assertThat(leads.contains(lead)).isTrue();
    }

    @Test
    @DisplayName("Should return unmodifiable set from findAll")
    void shouldReturnUnmodifiableSet() {
        // Given
        LeadRepository leads = new LeadRepository();
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact leadContact = new Contact("test@example.com", "+71234567890", address);
        //Конструктор без указания Id сам генерирует ключ
        Lead fistLead = new Lead(leadContact, "TechCorp", "NEW");
        Lead secondLead = new Lead(leadContact, "TechCorp", "NEW");

        // When
        leads.add(fistLead);

        // Then
        assertThatThrownBy(() -> leads.findAll().add(secondLead))
                .isInstanceOf(UnsupportedOperationException.class);

        // TODO: Given - добавить лид в репозиторий
        // TODO: When - вызвать findAll и попытаться изменить результат
        // TODO: Then - проверить что выбрасывается UnsupportedOperationException
    }

    @Test
    @DisplayName("Should perform contains() faster than ArrayList")
    void shouldPerformFasterThanArrayList() {
        // Given
        HashSet<Lead> hashSet = new HashSet<Lead>();
        ArrayList<Lead> arrayList = new ArrayList<Lead>();
        LeadRepository leads = new LeadRepository();
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact leadContact = new Contact("test@example.com", "+71234567890", address);
        Lead lead;

        for (int i = 0; i < 10000; i++) {
            lead = new Lead(leadContact, "TechCorp", "NEW");
            hashSet.add(lead);
            arrayList.add(lead);
        }

        Lead searchLead = arrayList.get(9999);

        // When
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            hashSet.contains(searchLead);
        }
        long hashSetTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            arrayList.contains(searchLead);
        }
        long arrayListTime = System.nanoTime() - start;

        // Then
        assertThat(hashSetTime < arrayListTime).isTrue();
    }
}