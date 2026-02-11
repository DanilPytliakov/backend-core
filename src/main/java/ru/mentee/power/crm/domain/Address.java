package ru.mentee.power.crm.domain;

import java.util.Objects;

public record Address(
        String city,
        String street,
        String zip
) {
    public Address(String city, String street, String zip) {
        this.city = Objects.requireNonNull(city, "Поле города не должно быть null");
        this.street = street;
        this.zip = Objects.requireNonNull(zip, "Поле почтового индекса не должно быть null");
    }
}
