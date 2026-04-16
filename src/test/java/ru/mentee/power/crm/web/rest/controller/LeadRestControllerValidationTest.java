package ru.mentee.power.crm.web.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.service.CompanyService;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.web.rest.mapper.LeadMapper;

@WebMvcTest(LeadRestController.class)
class LeadRestControllerValidationTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private LeadService leadService;
  @MockitoBean private CompanyService companyService;
  @MockitoBean private LeadMapper leadMapper;

  @ParameterizedTest(name = "[{index}] name=''{0}'', email=''{1}'' -> 400")
  @CsvSource(
      value = {"Alice| ", "Alice|alice-at-example.com", "A|alice@example.com"},
      delimiter = '|')
  void shouldReturn400_whenRequestIsInvalid(String name, String email) throws Exception {
    String requestBody =
        """
        {
          "name": "%s",
          "email": "%s",
          "status": "NEW"
        }
        """
            .formatted(name, email);

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(leadMapper, leadService, companyService);
  }

  @Test
  void shouldReturn201_whenAllFieldsAreValid() throws Exception {
    UUID leadId = UUID.randomUUID();
    UUID companyId = UUID.randomUUID();
    String requestBody =
        """
        {
          "name": "Alice",
          "email": "alice@example.com",
          "companyId": "%s",
          "status": "NEW"
        }
        """
            .formatted(companyId);

    Company company = new Company();
    company.setId(companyId);

    Lead mappedLead = new Lead("Alice", "alice@example.com", null, LeadStatus.NEW);
    Lead createdLead = new Lead("Alice", "alice@example.com", company, LeadStatus.NEW);
    createdLead.setId(leadId);

    LeadResponse response = new LeadResponse();
    response.setId(leadId);
    response.setName("Alice");
    response.setEmail("alice@example.com");
    response.companyId(companyId);
    response.setStatus(ru.mentee.power.crm.spring.dto.generated.LeadStatus.NEW);

    when(leadMapper.toEntity(any())).thenReturn(mappedLead);
    when(companyService.findById(companyId)).thenReturn(Optional.of(company));
    when(leadService.addLead(eq("Alice"), eq("alice@example.com"), eq(company), eq(LeadStatus.NEW)))
        .thenReturn(Optional.of(createdLead));
    when(leadMapper.toResponse(createdLead)).thenReturn(response);

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/leads/" + leadId))
        .andExpect(jsonPath("$.id").value(leadId.toString()))
        .andExpect(jsonPath("$.name").value("Alice"))
        .andExpect(jsonPath("$.email").value("alice@example.com"))
        .andExpect(jsonPath("$.companyId").value(companyId.toString()))
        .andExpect(jsonPath("$.status").value("NEW"));
  }
}
