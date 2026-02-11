package ru.mentee.power.crm.domain;

import java.util.Objects;
import java.util.UUID;

public record Customer (
        UUID id,
        Contact contact,
        Address billingAddress,
        String loyaltyTier
) {
    public Customer(UUID id, Contact contact, Address billingAddress, String loyaltyTier) {
        this.id = Objects.requireNonNull(id, "Поле с id не должно быть null");
        this.contact = Objects.requireNonNull(
                contact, "Поле с ссылкой на объект класса Contact электронной почты не должно быть null");
        this.billingAddress = Objects.requireNonNull(
                billingAddress, "Ссылка на объект Adress не должна быть null");

        if (loyaltyTier.equals("BRONZE") || loyaltyTier.equals("SILVER") || loyaltyTier.equals("GOLD")) {
            this.loyaltyTier = Objects.requireNonNull(
                    loyaltyTier, "Поле с номером телефона почты не должна быть null");
        }
        else {
            throw new IllegalArgumentException("Поле статуса не соттветствует приведенным стандартам"
                    + "\\nВозможные варианты: \"BRONZE\", \"SILVER\", \"GOLD\"");
        }
    }

    //Перегруженный конструктор для случаев, где мы не указываем idl, а он генерируется автоматически
    public Customer(Contact contact, Address billingAddress, String loyaltyTier) {
        this(UUID.randomUUID(), contact, billingAddress, loyaltyTier);
    }
};
