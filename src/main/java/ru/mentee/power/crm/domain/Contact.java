package ru.mentee.power.crm.domain;

import java.util.Objects;

public record Contact(
        String email,
        String phone,
        Address address
) {
    public Contact(String email, String phone, Address address) {
        this.email = Objects.requireNonNull(email, "Поле с адресом электронной почты не должна быть null");
        this.phone = Objects.requireNonNull(phone, "Поле с номером телефона почты не должна быть null");
        this.address = Objects.requireNonNull(address, "Ссылка на объект Adress не должна быть null");
    }
}
