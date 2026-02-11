package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ContactTest {

    @Test
    void shouldCreateContactWhenValidData() {
        // When
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("test@example.com", "+71234567890", address);

        //Then
        assertThat(contact.address()).isEqualTo(address);
        assertThat(contact.address().city()).isEqualTo("San Francisco");
    }

    @Test
    void shouldDelegateToAddressWhenAccessingCity() {
        // When
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("test@example.com", "+71234567890", address);

        //Then
        assertThat(contact.address().street()).isEqualTo("123 Main St");
        assertThat(contact.address().city()).isEqualTo("San Francisco");
    }

    @Test
    void shouldThrowExceptionWhenAddressIsNull() {
        // Аналогично с класcом Contact
        assertThatThrownBy(() -> new Contact("test@example.com", "+71234567890", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Ссылка на объект Adress не должна быть null");
    }
}