package ru.mentee.power.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompanyForm {

  @NotBlank(message = "{Comapany.name.notblank}")
  private String name;

  private String industry;

  public CreateCompanyForm(String name) {
    this.name = name;
  }
}
