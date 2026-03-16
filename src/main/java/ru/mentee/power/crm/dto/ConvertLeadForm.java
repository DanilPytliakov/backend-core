package ru.mentee.power.crm.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

@Data
@AllArgsConstructor
public class ConvertLeadForm {

    private UUID id;
    private String name;
    private LeadStatus status;

    public ConvertLeadForm(Lead lead) {
        this.id = lead.getId();
        this.name = lead.getName();
        this.status = lead.getStatus();
    }
}