package ru.mentee.power.crm.model;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class UpdateLeadForm extends CreateLeadForm {
    public UpdateLeadForm(UUID id, String email, String company, LeadStatus status) {
        super(email, company, status);
        this.id = id;
    }

    @NotNull
    private UUID id;

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    // геттер и сеттер для id
}