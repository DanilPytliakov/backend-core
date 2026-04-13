package ru.mentee.power.crm.web.mvc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentee.power.crm.domain.LeadStatus;

@Data
@NoArgsConstructor
public class CreateLeadForm {

  @NotBlank(message = "{lead.name.notblank}")
  private String name;

  @NotBlank(message = "{lead.email.notblank}")
  @Email(message = "{lead.email.format}")
  private String email;

  private UUID companyId;

  @NotNull(message = "{lead.status.notnull}")
  private LeadStatus status;
}
