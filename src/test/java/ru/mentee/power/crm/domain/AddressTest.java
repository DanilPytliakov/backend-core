package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    void shouldCreateAddressWhenValidData() {
        // When
        Address address = new Address("San Francisco", "123 Main St", "94105");

        //Then
        assertThat(address.city()).isEqualTo("San Francisco");
        assertThat(address.street()).isEqualTo("123 Main St");
        assertThat(address.zip()).isEqualTo("94105");
    }

    @Test
    void shouldBeEqualWhenSameData() {
        // When
        Address firstAddress = new Address("San Francisco", "123 Main St", "94105");
        Address secondAddress = new Address("San Francisco", "123 Main St", "94105");

        //then
        assertThat(firstAddress.equals(secondAddress)).isTrue();
        assertThat(firstAddress.hashCode()).isEqualTo(secondAddress.hashCode());
    }

    @Test
    void shouldThrowExceptionWhenCityIsNull() {
        // Я сделал проверку на NullPointerException потому что в конструкторах для обработки пустых полей
        // принято использовать Objects.requireNonNull
        assertThatThrownBy(() -> new Address(null, "123 Main St", "94105"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Поле города не должно быть null");
    }

    @Test
    void shouldThrowExceptionWhenZipIsBlank() {
        assertThatThrownBy(() -> new Address("San Francisco", "123 Main St", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Поле почтового индекса не должно быть null");
    }
}