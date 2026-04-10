package ru.mentee.power.crm.spring.config;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
        .connectTimeout(Duration.ofSeconds(5))
        .readTimeout(Duration.ofSeconds(10))
        .errorHandler(
            new DefaultResponseErrorHandler() {
              @Override
              protected boolean hasError(HttpStatusCode statusCode) {
                return statusCode.is4xxClientError() || statusCode.is5xxServerError();
              }
            })
        .build();
  }
}
