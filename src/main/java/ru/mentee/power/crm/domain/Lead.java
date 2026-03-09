package ru.mentee.power.crm.domain;

import java.util.Objects;
import java.util.UUID;

import ru.mentee.power.crm.dto.CreateLeadForm;

public record Lead(
        UUID id,
        String name,
        String email,
        String company,
        LeadStatus status
) {
    public Lead (UUID id, String name, String email, String company, LeadStatus status) {
        this.id = Objects.requireNonNull(id, "Поле с id не должно быть null");
        this.name = Objects.requireNonNull(name, "Поле с именем не должно быть null");
        this.email = Objects.requireNonNull(
                email, "Поле с адресом электронной почты не должно быть null");
        this.company = Objects.requireNonNull(company, "Поле с названием компани не должно быть null");
        this.status = Objects.requireNonNull(
                status, "Поле с информацией о статусе не должна быть null");
    }

    public Lead (String name, String email, String company, LeadStatus status) {
        this(UUID.randomUUID(), name, email, company, status);
    }

    public Lead (CreateLeadForm form) {
        this(UUID.randomUUID(), form.getName(), form.getEmail(), form.getCompany(), form.getStatus());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Lead lead = (Lead) o;
        return Objects.equals(id, lead.id) && Objects.equals(email, lead.email)
                && Objects.equals(company, lead.company) && Objects.equals(status, lead.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, company, status);
    }
};

