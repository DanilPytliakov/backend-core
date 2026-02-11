package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class LeadTest {

    @Test
    void shouldCreateLeadWhenValidData() {
        // When
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("test@example.com", "+71234567890", address);
        Lead lead = new Lead(contact, "TechCorp", "NEW");

        // Then
        assertThat(lead.contact()).isEqualTo(contact);
    }

    @Test
    void shouldAccessEmailThroughDelegationWhenLeadCreated() {
        // When
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("test@example.com", "+71234567890", address);
        Lead lead = new Lead(contact, "TechCorp", "NEW");

        // Then
        assertThat(lead.contact().email()).isEqualTo("test@example.com");
        assertThat(lead.contact().address().city()).isEqualTo("San Francisco");
    }

    @Test
    void shouldBeEqualWhenSameIdButDifferentContact() {
        // When
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact firstLeadContact = new Contact("test@example.com", "+71234567890", address);
        Contact secondLeadContact = new Contact("test@mail.ru", "+71298765432", address);
        Lead firstLead = new Lead(firstLeadContact, "TechCorp", "NEW");
        Lead secondLead = new Lead(firstLead.id(), secondLeadContact, "TechCorp", "NEW");

        //Then
        assertThat(firstLead.equals(secondLead)).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenContactIsNull() {
        // Аналогично с класcом Contact
        assertThatThrownBy(() -> new Lead(null, "TechCorp", "NEW"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Поле с ссылкой на объект класса Contact электронной почты не должно быть null");
    }

    @Test
    void shouldThrowExceptionWhenInvalidStatus() {
        // When
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("test@example.com", "+71234567890", address);
        assertThatThrownBy(() -> new Lead(contact, "TechCorp", "OLD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Поле статуса не соттветствует приведенным стандартам"
                        + "\\nВозможные варианты: \"NEW\", \"QUALIFIED\", \"CONVERTED\"");
    }

    @Test
    void shouldDemonstrateThreeLevelCompositionWhenAccessingCity() {
        // When
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("test@example.com", "+71234567890", address);
        Lead lead = new Lead(contact, "TechCorp", "NEW");

        // Then
        assertThat(lead.contact().address().city()).isEqualTo("San Francisco");
    }
}