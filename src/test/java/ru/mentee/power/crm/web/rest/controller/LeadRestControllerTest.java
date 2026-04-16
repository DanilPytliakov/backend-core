package ru.mentee.power.crm.web.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.exception.EntityNotFoundException;
import ru.mentee.power.crm.service.CompanyService;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.web.rest.mapper.LeadMapper;

@WebMvcTest(LeadRestController.class)
class LeadRestControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private LeadService leadService;
  @MockitoBean private CompanyService companyService;
  @MockitoBean private LeadMapper leadMapper;

  @Test
  void shouldReturn200_whenGetAllLeads() throws Exception {
    Lead lead = new Lead("Alice", "alice@example.com", null, LeadStatus.NEW);
    LeadResponse response = new LeadResponse();
    response.setName("Alice");
    response.setEmail("alice@example.com");
    response.setStatus(ru.mentee.power.crm.spring.dto.generated.LeadStatus.NEW);
    when(leadService.findAll()).thenReturn(List.of(lead));
    when(leadMapper.toResponse(any(Lead.class))).thenReturn(response);

    mockMvc
        .perform(get("/api/leads"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void shouldReturn404_whenGetNonExistentLead() throws Exception {
    UUID id = UUID.randomUUID();
    when(leadService.getLeadOrThrow(id))
        .thenThrow(new EntityNotFoundException("Lead", id.toString()));

    mockMvc.perform(get("/api/leads/{id}", id)).andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn201WithLocation_whenCreateLead() throws Exception {
    UUID id = UUID.randomUUID();
    Lead createdLead = new Lead("Alice", "alice@example.com", null, LeadStatus.NEW);
    createdLead.setId(id);
    LeadResponse response = new LeadResponse();
    response.setId(id);
    response.setName("Alice");
    response.setEmail("alice@example.com");
    response.setStatus(ru.mentee.power.crm.spring.dto.generated.LeadStatus.NEW);
    when(leadMapper.toEntity(any()))
        .thenReturn(new Lead("Alice", "alice@example.com", null, LeadStatus.NEW));
    when(leadService.addLead("Alice", "alice@example.com", null, LeadStatus.NEW))
        .thenReturn(Optional.of(createdLead));
    when(leadMapper.toResponse(createdLead)).thenReturn(response);

    mockMvc
        .perform(
            post("/api/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice\",\"email\":\"alice@example.com\",\"status\":\"NEW\"}"))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(header().string("Location", "/api/leads/" + id));
  }

  @Test
  void shouldReturn204_whenDeleteExistingLead() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc
        .perform(delete("/api/leads/{id}", id))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
  }

  @Test
  void shouldReturn404_whenDeleteNonExistentLead() throws Exception {
    UUID id = UUID.randomUUID();
    org.mockito.Mockito.doThrow(new EntityNotFoundException("Lead", id.toString()))
        .when(leadService)
        .deleteLeadOrThrow(id);

    mockMvc.perform(delete("/api/leads/{id}", id)).andExpect(status().isNotFound());
  }
}
