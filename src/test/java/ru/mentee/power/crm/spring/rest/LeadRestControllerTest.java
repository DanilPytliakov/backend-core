package ru.mentee.power.crm.spring.rest;

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
import ru.mentee.power.crm.service.LeadService;

@WebMvcTest(LeadRestController.class)
class LeadRestControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private LeadService leadService;

  @Test
  void shouldReturn200_whenGetAllLeads() throws Exception {
    when(leadService.findAll()).thenReturn(List.of());

    mockMvc
        .perform(get("/api/leads"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void shouldReturn404_whenGetNonExistentLead() throws Exception {
    UUID id = UUID.randomUUID();
    when(leadService.findById(id)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/leads/{id}", id)).andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn201WithLocation_whenCreateLead() throws Exception {
    UUID id = UUID.randomUUID();
    Lead createdLead = new Lead("Alice", "alice@example.com", null, LeadStatus.NEW);
    createdLead.setId(id);
    when(leadService.addLead(any(Lead.class))).thenReturn(Optional.of(createdLead));

    mockMvc
        .perform(
            post("/api/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice\",\"email\":\"alice@example.com\"}"))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(header().string("Location", "/api/leads/" + id));
  }

  @Test
  void shouldReturn204_whenDeleteExistingLead() throws Exception {
    UUID id = UUID.randomUUID();
    when(leadService.deleteLead(id)).thenReturn(true);

    mockMvc
        .perform(delete("/api/leads/{id}", id))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
  }

  @Test
  void shouldReturn404_whenDeleteNonExistentLead() throws Exception {
    UUID id = UUID.randomUUID();
    when(leadService.deleteLead(id)).thenReturn(false);

    mockMvc.perform(delete("/api/leads/{id}", id)).andExpect(status().isNotFound());
  }
}
