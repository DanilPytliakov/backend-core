package ru.mentee.power.crm.spring.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.repository.CompanyRepository;
import ru.mentee.power.crm.service.CompanyService;
import ru.mentee.power.crm.service.DealService;
import ru.mentee.power.crm.service.LeadService;

@SpringBootTest(properties = "gg.jte.template-location=src/main/jte")
@AutoConfigureMockMvc
class DealControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private DealService dealService;
    @Autowired private LeadService leadService;
    @Autowired private CompanyRepository companyRepository;

    private Company company =  new Company();

    private Lead createLead() {
        company = companyRepository.save(new Company("FirstCompany", "buisines"));
        return leadService.addLead(
                "Danil",
                UUID.randomUUID() + "@test.com",
                company
        ).get();
    }

    private Deal createDeal(UUID leadId) {
        return dealService.convertLeadToDeal(leadId, BigDecimal.valueOf(10000));
    }

    @Test
    void listDeals_shouldReturnDealsView() throws Exception {
        mockMvc.perform(get("/deals"))
                .andExpect(status().isOk())
                .andExpect(view().name("deals/list"))
                .andExpect(model().attributeExists("deals"));
    }

    @Test
    void kanbanView_shouldReturnKanbanView() throws Exception {
        mockMvc.perform(get("/deals/kanban"))
                .andExpect(status().isOk())
                .andExpect(view().name("deals/kanban"))
                .andExpect(model().attributeExists("dealsByStatus"));
    }

    @Test
    void showConvertForm_shouldReturnConvertView() throws Exception {
        Lead lead = createLead();

        mockMvc.perform(get("/deals/convert/{leadId}", lead.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("deals/convert"))
                .andExpect(model().attributeExists("leadForm"));
    }

    @Test
    void convertLeadToDeal_shouldRedirectToDeals() throws Exception {
        Lead lead = createLead();

        mockMvc.perform(post("/deals/convert")
                        .param("leadId", lead.getId().toString())
                        .param("amount", "15000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/deals"));
    }

    @Test
    void getAvailableTransitions_shouldReturnTransitionsJson() throws Exception {
        Lead lead = createLead();
        Deal deal = createDeal(lead.getId());

        mockMvc.perform(get("/deals/{id}/transitions", deal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", containsInAnyOrder("QUALIFIED", "LOST")));
    }

    @Test
    void transitionStatus_shouldRedirectToDeals() throws Exception {
        Lead lead = createLead();
        Deal deal = createDeal(lead.getId());

        mockMvc.perform(post("/deals/{id}/transition", deal.getId())
                        .param("newStatus", "QUALIFIED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/deals"));
    }

    @Test
    void transitionStatus_toTerminalState_shouldRedirectToDeals() throws Exception {
        Lead lead = createLead();
        Deal deal = createDeal(lead.getId());

        mockMvc.perform(post("/deals/{id}/transition", deal.getId())
                        .param("newStatus", "LOST"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/deals"));
    }

    @Test
    void getAvailableTransitions_forWonDeal_shouldReturnEmptyArray() throws Exception {
        Lead lead = createLead();
        Deal deal = createDeal(lead.getId());
        dealService.transitionDealStatus(deal.getId(), DealStatus.QUALIFIED);
        dealService.transitionDealStatus(deal.getId(), DealStatus.PROPOSAL_SENT);
        dealService.transitionDealStatus(deal.getId(), DealStatus.NEGOTIATION);
        dealService.transitionDealStatus(deal.getId(), DealStatus.WON);

        mockMvc.perform(get("/deals/{id}/transitions", deal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}