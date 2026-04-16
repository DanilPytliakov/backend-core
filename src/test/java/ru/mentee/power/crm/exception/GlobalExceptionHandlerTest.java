package ru.mentee.power.crm.exception;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.service.CompanyService;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.web.rest.controller.LeadRestController;
import ru.mentee.power.crm.web.rest.mapper.LeadMapper;

@WebMvcTest(LeadRestController.class)
class GlobalExceptionHandlerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private LeadService leadService;
  @MockitoBean private CompanyService companyService;
  @MockitoBean private LeadMapper leadMapper;

  @Test
  void shouldReturn404_whenEntityNotFound() throws Exception {
    UUID id = UUID.randomUUID();
    when(leadService.getLeadOrThrow(id))
        .thenThrow(new EntityNotFoundException("Lead", id.toString()));

    mockMvc
        .perform(get("/api/leads/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Lead not found with id: " + id))
        .andExpect(jsonPath("$.path").value("/api/leads/" + id));
  }

  @Test
  void shouldReturn400WithFieldErrors_whenValidationFails() throws Exception {
    String invalidBody =
        """
        {
          "name": "A",
          "email": "bad-email",
          "status": "NEW"
        }
        """;

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(invalidBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.path").value("/api/leads"))
        .andExpect(jsonPath("$.errors.name").exists())
        .andExpect(jsonPath("$.errors.email").exists());
  }

  @Test
  void shouldReturn500_whenUnexpectedExceptionOccurs() throws Exception {
    UUID id = UUID.randomUUID();
    when(leadService.getLeadOrThrow(id))
        .thenThrow(new RuntimeException("database credentials leaked"));

    mockMvc
        .perform(get("/api/leads/{id}", id))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.error").value("Internal Server Error"))
        .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
        .andExpect(jsonPath("$.path").value("/api/leads/" + id));
  }
}
