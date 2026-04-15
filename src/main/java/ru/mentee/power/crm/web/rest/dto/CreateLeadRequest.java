package ru.mentee.power.crm.web.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentee.power.crm.domain.LeadStatus;

@Data
@NoArgsConstructor
public class CreateLeadRequest {

  @NotBlank(message = "Имя обязательно")
  @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
  private String name;

  @NotBlank(message = "Email обязателен")
  @Email(message = "Email должен быть в корректном формате")
  private String email;

  private UUID companyId;

  @NotNull(message = "Статус обязателен")
  private LeadStatus status;
}
