package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LeadTest {
    @Test
    void shouldReturnIdWhenGetIdCalled() {
        // Given
        Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String id = lead.getId();

        // Then
        assertThat(id).isEqualTo("L1");
    }

    @Test
    void shouldReturnEmailWhenGetEmailCalled() {
        // Given
        Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String email = lead.getEmail();

        // Then
        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    void shouldReturnCompanyWhenGetCompanyCalled() {
        // Given
        Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String company = lead.getCompany();

        // Then
        assertThat(company).isEqualTo("TestCorp");
    }

    @Test
    void shouldReturnStatusWhenGetStatusCalled() {
        // Given
        Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String status = lead.getStatus();

        // Then
        assertThat(status).isEqualTo("NEW");
    }

    @Test
    void shouldReturnPhoneWhenGetPhoneCalled() {
        // Given
        Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String phone = lead.getPhone();

        // Then
        assertThat(phone).isEqualTo("+71234567890");
    }

    @Test
    void shouldReturnFormattedStringWhenToStringCalled() {
        Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");
        String testLead = "{id: L1 email: test@example.com phone: +71234567890 company: TestCorp status: NEW}";
        assertThat(testLead).isEqualTo(lead.toString());
    }
}