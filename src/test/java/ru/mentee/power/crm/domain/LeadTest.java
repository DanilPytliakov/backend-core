package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.UUID;

class LeadTest {
    @Test
    void shouldReturnEmailWhenGetEmailCalled() {
        // Given
        Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String email = lead.email();

        // Then
        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    void shouldReturnCompanyWhenGetCompanyCalled() {
        // Given
        Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String company = lead.company();

        // Then
        assertThat(company).isEqualTo("TestCorp");
    }

    @Test
    void shouldReturnStatusWhenGetStatusCalled() {
        // Given
        Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String status = lead.status();

        // Then
        assertThat(status).isEqualTo("NEW");
    }

    @Test
    void shouldReturnPhoneWhenGetPhoneCalled() {
        // Given
        Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String phone = lead.phone();

        // Then
        assertThat(phone).isEqualTo("+71234567890");
    }

    @Test
    void shouldReturnFormattedStringWhenToStringCalled() {
        Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");
        String testLead = "Lead[id=" + lead.id() + ", email=test@example.com, phone=+71234567890, company=TestCorp, status=NEW]";
        assertThat(testLead).isEqualTo(lead.toString());
    }

    //Метод для теста
    boolean findById(Lead leadForCompare, UUID id){
        if(Objects.equals(leadForCompare.id(), id)){
            return true;
        }
        return false;
    }

    @Test
    void shouldPreventStringConfusion_whenUsingUUID() {
        // Given
        Lead leadForCompare = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        assertThat(findById(leadForCompare, leadForCompare.id())).isTrue();

        // assertThat(findById(leadForCompare, "SomeString").isTrue(); не запускается
    }
}