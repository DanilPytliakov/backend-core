package ru.mentee.power.crm.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public class CreateLeadForm {
    public CreateLeadForm(String email, String company, LeadStatus status) {
        this.email = email;
        this.company = company;
        this.status = status;
    }
    public CreateLeadForm() {
    }

    @NotBlank(message = "{lead.email.notblank}")
    @Email(message = "{lead.email.format}")
    private String email;

    @NotBlank(message = "{lead.company.notblank}")
    private String company;

    @NotNull(message = "{lead.status.notnull}")
    private LeadStatus status;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }

    public LeadStatus getStatus() {
        return status;
    }
    public void setStatus(LeadStatus status) {
        this.status = status;
    }
}