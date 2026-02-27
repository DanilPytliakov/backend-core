package ru.mentee.power.crm.spring.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "gg.jte.template-location=src/main/jte")
@AutoConfigureMockMvc
class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenSpringContext_whenGetLeads_thenStatus200AndContainsEmail() throws Exception {
        // Given Spring context is loaded

        // When GET /leads
        mockMvc.perform(get("/leads"))
                // Then status 200 OK
                .andExpect(status().isOk())
                // And response contains "email"
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Email")));
    }
}