package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void shouldReuseContactWhenCreatingCustomer() {
        // When
        Address contactAddress = new Address("San Francisco", "123 Main St", "94105");
        Address billingAddress = new Address("Los Angeles", "14 Gold St", "36821");
        Contact contact = new Contact("test@example.com", "+71234567890", contactAddress);
        Customer customer = new Customer(contact, billingAddress, "BRONZE");

        //Then
        assertThat(customer.contact().address()).isNotEqualTo(customer.billingAddress());
    }

    @Test
    void shouldDemonstrateContactReuseAcrossLeadAndCustomer() {
        // When
        Address contactAddress = new Address("San Francisco", "123 Main St", "94105");
        Address billingAddress = new Address("Los Angeles", "14 Gold St", "36821");
        Contact contact = new Contact("test@example.com", "+71234567890", contactAddress);
        Customer customer = new Customer(contact, billingAddress, "BRONZE");
        Lead lead = new Lead(contact, "TechCorp", "NEW");

        // Then
        assertThat(customer.contact()).isEqualTo(lead.contact());
    }
}