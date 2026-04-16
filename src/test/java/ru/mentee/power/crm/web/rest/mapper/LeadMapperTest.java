package ru.mentee.power.crm.web.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.spring.dto.generated.UpdateLeadRequest;

@SpringBootTest
class LeadMapperTest {

  @Autowired private LeadMapper leadMapper;

  // ───── toEntity (CreateLeadRequest → Lead) ─────

  @Test
  void shouldMapCreateRequestToEntity_whenValidData() {
    // Given
    CreateLeadRequest request = new CreateLeadRequest();
    request.setName("Иван Иванов");
    request.setEmail("ivan@example.com");
    request.setCompanyId(JsonNullable.of(UUID.randomUUID()));
    request.setStatus(ru.mentee.power.crm.spring.dto.generated.LeadStatus.NEW);

    // When
    Lead lead = leadMapper.toEntity(request);

    // Then
    assertThat(lead).isNotNull();
    assertThat(lead.getId()).isNull(); // id игнорируется
    assertThat(lead.getCreatedAt()).isNull(); // createdAt игнорируется
    assertThat(lead.getCompany()).isNull(); // company игнорируется
    assertThat(lead.getName()).isEqualTo("Иван Иванов");
    assertThat(lead.getEmail()).isEqualTo("ivan@example.com");
    assertThat(lead.getStatus()).isEqualTo(LeadStatus.NEW);
  }

  @Test
  void shouldMapCreateRequestToEntity_whenNullableFieldsAreNull() {
    // Given
    CreateLeadRequest request = new CreateLeadRequest();
    request.setName("Пётр");
    request.setEmail("petr@example.com");
    request.setCompanyId(JsonNullable.undefined());
    // status не задан

    // When
    Lead lead = leadMapper.toEntity(request);

    // Then
    assertThat(lead).isNotNull();
    assertThat(lead.getName()).isEqualTo("Пётр");
    assertThat(lead.getEmail()).isEqualTo("petr@example.com");
    assertThat(lead.getCompany()).isNull();
    assertThat(lead.getId()).isNull();
    assertThat(lead.getStatus()).isNull();
  }

  // ───── toResponse (Lead → LeadResponse) ─────

  @Test
  void shouldMapEntityToResponse_whenValidEntity() {
    // Given
    UUID leadId = UUID.randomUUID();
    UUID companyId = UUID.randomUUID();
    LocalDateTime createdAt = LocalDateTime.of(2026, 1, 15, 10, 30);

    Company company = new Company("Яндекс", "IT");
    // Устанавливаем id компании через рефлексию или сеттер если есть
    company.setId(companyId);

    Lead lead = new Lead("Мария Петрова", "maria@example.com", company);
    lead.setId(leadId);
    lead.setStatus(LeadStatus.CONTACTED);
    lead.setCreatedAt(createdAt);

    // When
    LeadResponse response = leadMapper.toResponse(lead);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(leadId);
    assertThat(response.getName()).isEqualTo("Мария Петрова");
    assertThat(response.getEmail()).isEqualTo("maria@example.com");
    assertThat(response.getCompanyId().get()).isEqualTo(companyId);
    assertThat(response.getStatus())
        .isEqualTo(ru.mentee.power.crm.spring.dto.generated.LeadStatus.CONTACTED);
    assertThat(response.getCreatedAt()).isEqualTo(createdAt.atOffset(ZoneOffset.UTC));
  }

  @Test
  void shouldMapEntityToResponse_whenCompanyIsNull() {
    // Given
    UUID leadId = UUID.randomUUID();
    Lead lead = new Lead("Алексей", "alex@example.com", null);
    lead.setId(leadId);
    lead.setStatus(LeadStatus.NEW);
    lead.setCreatedAt(LocalDateTime.now());

    // When
    LeadResponse response = leadMapper.toResponse(lead);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(leadId);
    assertThat(response.getCompanyId().isPresent()).isFalse(); // company null → companyId undefined
    assertThat(response.getName()).isEqualTo("Алексей");
  }

  // ───── updateEntity (UpdateLeadRequest → Lead) ─────

  @Test
  void shouldUpdateEntityFromRequest_whenValidData() {
    // Given — существующий лид
    Lead existingLead = new Lead("Старое имя", "old@example.com", null);
    existingLead.setId(UUID.randomUUID());
    existingLead.setStatus(LeadStatus.NEW);

    UpdateLeadRequest request = new UpdateLeadRequest();
    request.setName("Новое имя");
    request.setEmail("new@example.com");
    request.setStatus(ru.mentee.power.crm.spring.dto.generated.LeadStatus.QUALIFIED);

    // When
    leadMapper.updateEntity(request, existingLead);

    // Then
    assertThat(existingLead.getName()).isEqualTo("Новое имя");
    assertThat(existingLead.getEmail()).isEqualTo("new@example.com");
    assertThat(existingLead.getStatus()).isEqualTo(LeadStatus.QUALIFIED);
    assertThat(existingLead.getId()).isNotNull(); // id не трогается
    assertThat(existingLead.getCompany()).isNull(); // company не трогается
  }

  @Test
  void shouldNotOverwriteIdAndCreatedAt_whenUpdateEntityCalled() {
    // Given
    UUID originalId = UUID.randomUUID();
    LocalDateTime originalCreatedAt = LocalDateTime.of(2025, 6, 1, 12, 0);

    Lead existingLead = new Lead("Имя", "email@example.com", null);
    existingLead.setId(originalId);
    existingLead.setCreatedAt(originalCreatedAt);

    UpdateLeadRequest request = new UpdateLeadRequest();
    request.setName("Другое имя");
    request.setEmail("other@example.com");
    request.setStatus(ru.mentee.power.crm.spring.dto.generated.LeadStatus.CONTACTED);

    // When
    leadMapper.updateEntity(request, existingLead);

    // Then — id и createdAt остались без изменений
    assertThat(existingLead.getId()).isEqualTo(originalId);
    assertThat(existingLead.getCreatedAt()).isEqualTo(originalCreatedAt);
  }
}
