package ru.mentee.power.crm.web.rest.dto;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentee.power.crm.domain.LeadStatus;

@Data
@NoArgsConstructor
public class UpdateLeadRequest {

  private String name;
  private String email;
  private UUID companyId;
  private LeadStatus status;
}
