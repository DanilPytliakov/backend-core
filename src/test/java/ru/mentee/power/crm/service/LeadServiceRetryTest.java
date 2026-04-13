package ru.mentee.power.crm.service;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.repository.CompanyRepository;

@SpringBootTest
@WireMockTest(httpPort = LeadServiceRetryTest.WIREMOCK_PORT)
class LeadServiceRetryTest {

  static final int WIREMOCK_PORT = 18_090;

  @Autowired private LeadService leadService;
  @Autowired private CompanyRepository companyRepository;

  private Company company;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("email.validation.base-url", () -> "http://localhost:" + WIREMOCK_PORT);
    registry.add("resilience4j.retry.instances.email-validation.max-attempts", () -> "3");
    registry.add("resilience4j.retry.instances.email-validation.wait-duration", () -> "10ms");
    registry.add("spring.cloud.openfeign.client.config.email-validation.read-timeout", () -> "200");
    registry.add(
        "spring.cloud.openfeign.client.config.email-validation.connect-timeout", () -> "200");
  }

  @BeforeEach
  void setUp() {
    company = companyRepository.save(new Company("Retry test company", "qa"));
  }

  @Test
  void shouldUseFallbackWithoutRetry_whenServerErrorOccurs() {
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("retry-success@example.com"))
            .inScenario("Retry Success")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(serverError())
            .willSetStateTo("second-attempt"));

    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("retry-success@example.com"))
            .inScenario("Retry Success")
            .whenScenarioStateIs("second-attempt")
            .willReturn(serverError())
            .willSetStateTo("third-attempt"));

    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("retry-success@example.com"))
            .inScenario("Retry Success")
            .whenScenarioStateIs("third-attempt")
            .willReturn(
                okJson(
                    """
                    {
                      "email": "retry-success@example.com",
                      "valid": true,
                      "reason": "OK"
                    }
                    """)));

    Optional<Lead> createdLead =
        leadService.addLead("Retry Success", "retry-success@example.com", company);

    assertThat(createdLead).isPresent();
    assertThat(createdLead.orElseThrow().getEmail()).isEqualTo("retry-success@example.com");
    verify(
        1,
        getRequestedFor(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("retry-success@example.com")));
  }

  @Test
  void shouldUseFallback_whenServerErrorOccurs() {
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("fallback@example.com"))
            .willReturn(serverError().withBody("Service Unavailable")));

    Optional<Lead> createdLead = leadService.addLead("Fallback", "fallback@example.com", company);

    assertThat(createdLead).isPresent();
    assertThat(createdLead.orElseThrow().getEmail()).isEqualTo("fallback@example.com");
    verify(
        1,
        getRequestedFor(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("fallback@example.com")));
  }

  @Test
  void shouldUseFallbackAfterTwoAttempts_whenClientErrorOccurs() {
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("bad-request@example.com"))
            .willReturn(badRequest().withBody("{\"error\": \"Invalid format\"}")));

    Optional<Lead> createdLead =
        leadService.addLead("Bad Request", "bad-request@example.com", company);

    assertThat(createdLead).isPresent();
    verify(
        2, // BadRequest не ретраится — это правильное поведение
        getRequestedFor(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("bad-request@example.com")));
  }

  @Test
  void shouldRetry_whenTimeoutOccurs() {
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("timeout@example.com"))
            .inScenario("Timeout Retry")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(ok().withFixedDelay(500))
            .willSetStateTo("after-timeout"));

    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("timeout@example.com"))
            .inScenario("Timeout Retry")
            .whenScenarioStateIs("after-timeout")
            .willReturn(
                okJson(
                    """
                    {
                      "email": "timeout@example.com",
                      "valid": true,
                      "reason": "OK"
                    }
                    """)));

    Optional<Lead> createdLead = leadService.addLead("Timeout", "timeout@example.com", company);

    assertThat(createdLead).isPresent();
    verify(
        2,
        getRequestedFor(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("timeout@example.com")));
  }
}
