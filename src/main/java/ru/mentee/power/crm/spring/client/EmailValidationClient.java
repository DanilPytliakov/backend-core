package ru.mentee.power.crm.spring.client;

import org.springframework.stereotype.Component;

@Component
public class EmailValidationClient {

  private final EmailValidationFeignClient feignClient;

  public EmailValidationClient(EmailValidationFeignClient feignClient) {
    this.feignClient = feignClient;
  }

  public EmailValidationResponse validateEmail(String email) {
    try {
      EmailValidationResponse response = feignClient.validateEmail(email);
      if (response == null) {
        throw new IllegalStateException("Email validation service returned empty response");
      }
      return response;
    } catch (Exception ex) {
      throw new IllegalStateException(
          "Failed to validate email via external service: " + email, ex);
    }
  }
}
