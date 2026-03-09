package ru.mentee.power.crm.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.mentee.power.crm.domain.Lead;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateLeadForm extends CreateLeadForm {

    @NotNull
    private UUID id;

    public UpdateLeadForm(Lead lead) {
        setName(lead.name());
        setEmail(lead.email());
        setCompany(lead.company());
        setStatus(lead.status());
        this.id = lead.id();
    }
}