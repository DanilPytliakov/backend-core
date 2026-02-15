package ru.mentee.power.crm.domain;

import java.util.Objects;
import java.util.UUID;

public record Lead (
    UUID id,
    Contact contact,
    String company,
    String status
) {
    public Lead(UUID id, Contact contact, String company, String status) {
        this.id = Objects.requireNonNull(id, "Поле с id не должно быть null");
        this.contact = Objects.requireNonNull(
                contact, "Поле с ссылкой на объект класса Contact электронной почты не должно быть null");
        this.company = Objects.requireNonNull(company, "Поле с названием компани не должно быть null");

        if (status.equals("NEW") || status.equals("QUALIFIED") || status.equals("CONVERTED")) {
            this.status = Objects.requireNonNull(status, "Поле с номером телефона почты не должна быть null");
        }
        else {
            throw new IllegalArgumentException("Поле статуса не соттветствует приведенным стандартам"
                    + "\\nВозможные варианты: \"NEW\", \"QUALIFIED\", \"CONVERTED\"");
        }
    }

    public Lead(Contact contact, String company, String status) {
        this(UUID.randomUUID(), contact, company, status);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Lead lead = (Lead) o;
        return Objects.equals(id, lead.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
};
