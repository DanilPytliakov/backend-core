package ru.mentee.power.crm.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.mentee.power.crm.domain.Lead;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateLeadForm extends CreateLeadForm {

  @NotNull private UUID id;

  public UpdateLeadForm(Lead lead) {
    setName(lead.getName());
    setEmail(lead.getEmail());
    setCompanyId(lead.getCompany() != null ? lead.getCompany().getId() : null);
    setStatus(lead.getStatus());
    this.id = lead.getId();
  }
}
