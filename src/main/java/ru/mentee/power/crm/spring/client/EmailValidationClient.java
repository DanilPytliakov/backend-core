package ru.mentee.power.crm.spring.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class EmailValidationClient {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public EmailValidationClient(
      RestTemplate restTemplate,
      @Value("${email.validation.base-url:http://localhost:8082}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public EmailValidationResponse validateEmail(String email) {
    String url =
        UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/api/validate/email")
            .queryParam("email", email)
            .toUriString();

    try {
      EmailValidationResponse response =
          restTemplate.getForObject(url, EmailValidationResponse.class);
      if (response == null) {
        throw new IllegalStateException("Email validation service returned empty response");
      }
      return response;
    } catch (RestClientException ex) {
      throw new IllegalStateException(
          "Failed to validate email via external service: " + email, ex);
    }
  }
}
