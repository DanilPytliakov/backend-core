package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;

class LeadServiceTest {

    private LeadService service;
    private InMemoryLeadRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLeadRepository();
        service = new LeadService(repository);
    }

    @Test
    void shouldCreateLead_whenEmailIsUnique() {
        // Given
        String email = "test@example.com";
        String company = "Test Company";
        LeadStatus status = LeadStatus.NEW;

        // When
        Lead result = service.addLead(email, company, status);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.company()).isEqualTo(company);
        assertThat(result.status()).isEqualTo(status);
        assertThat(result.id()).isNotNull();
    }

    @Test
    void shouldThrowException_whenEmailAlreadyExists() {
        // Given
        String email = "duplicate@example.com";
        service.addLead(email, "First Company", LeadStatus.NEW);

        // When/Then
        assertThatThrownBy(() ->
                service.addLead(email, "Second Company", LeadStatus.NEW)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Lead with email already exists");
    }

    @Test
    void shouldFindAllLeads() {
        // Given
        service.addLead("one@example.com", "Company 1", LeadStatus.NEW);
        service.addLead("two@example.com", "Company 2", LeadStatus.CONTACTED);

        // When
        List<Lead> result = service.findAll();

        // Then
        assertThat(result).hasSize(2);
    }

    @Test
    void shouldFindLeadById() {
        // Given
        Lead created = service.addLead("find@example.com", "Company", LeadStatus.NEW);

        // When
        Optional<Lead> result = service.findById(created.id());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo("find@example.com");
    }

    @Test
    void shouldFindLeadByEmail() {
        // Given
        service.addLead("search@example.com", "Company", LeadStatus.NEW);

        // When
        Optional<Lead> result = service.findByEmail("search@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().company()).isEqualTo("Company");
    }

    @Test
    void shouldReturnEmpty_whenLeadNotFound() {
        // Given/When
        Optional<Lead> result = service.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    // Тест на сохранение лида с неверным значение входных данных
    @Test
    void shouldThrowException_whenWeTryToSaveLeedWithNullElements() {
        // When/Then
        assertThatThrownBy(() ->
                service.addLead("test@example.com", null, LeadStatus.NEW)
        )
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Поле с названием компани не должно быть null");

        // Исключение выкидывается ещё на этапе создания объекта
        assertThatThrownBy(() ->
                new Lead("test@example.com", null, LeadStatus.NEW))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Поле с названием компани не должно быть null");
    }

    // Метод для создания Лидов с конкретным параметром, конкретное количество раз
    private void addLeads(LeadService service, int count, LeadStatus status) {
        for (int i = 0; i < count; i++) {
            service.addLead(
                    "user" + status + i + "@gmail.com",
                    "Company" + i,
                    status
            );
        }
    }

    @Test
    void shouldReturnOnlyNewLeads_whenFindByStatusNew() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        LeadService leadService = new LeadService(repository);

        addLeads(leadService, 3, LeadStatus.NEW);
        addLeads(leadService, 5, LeadStatus.CONTACTED);
        addLeads(leadService, 2, LeadStatus.QUALIFIED);

        // When
        List<Lead> result = leadService.findByStatus(LeadStatus.NEW);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).allMatch(lead -> lead.status().equals(LeadStatus.NEW));
    }

    @Test
    void shouldReturnEmptyList_whenNoLeadsWithStatusQualified() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        LeadService leadService = new LeadService(repository);

        addLeads(leadService, 3, LeadStatus.NEW);
        addLeads(leadService, 5, LeadStatus.CONTACTED);

        // When
        List<Lead> result = leadService.findByStatus(LeadStatus.QUALIFIED);

        // Then
        assertThat(result).hasSize(0);
    }

    @Test
    void shouldReturnEmptyList_whenNoLeadsWithStatusNew() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        LeadService leadService = new LeadService(repository);

        addLeads(leadService, 3, LeadStatus.QUALIFIED);
        addLeads(leadService, 5, LeadStatus.CONTACTED);

        // When
        List<Lead> result = leadService.findByStatus(LeadStatus.NEW);

        // Then
        assertThat(result).hasSize(0);
    }

    @Test
    void shouldReturnEmptyList_whenNoLeadsWithStatusContacted() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        LeadService leadService = new LeadService(repository);

        addLeads(leadService, 3, LeadStatus.NEW);
        addLeads(leadService, 5, LeadStatus.QUALIFIED);

        // When
        List<Lead> result = leadService.findByStatus(LeadStatus.CONTACTED);

        // Then
        assertThat(result).hasSize(0);
    }
}