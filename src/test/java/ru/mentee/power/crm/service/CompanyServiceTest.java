package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.web.mvc.dto.CreateCompanyForm;

@SpringBootTest
@Transactional
class CompanyServiceTest {

  @Autowired private CompanyService companyService;

  @Autowired private LeadService leadService;

  @Test
  void givenValidForm_whenAddCompany_thenCompanyCreated() {
    // Given
    CreateCompanyForm form = new CreateCompanyForm("Яндекс", "IT");

    // When
    Optional<Company> result = companyService.addCompany(form);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Яндекс");
    assertThat(result.get().getIndustry()).isEqualTo("IT");
    assertThat(result.get().getId()).isNotNull();
  }

  @Test
  void givenDuplicateName_whenAddCompany_thenReturnEmpty() {
    // Given
    CreateCompanyForm form = new CreateCompanyForm("Сбер", "Финансы");
    companyService.addCompany(form);

    // When — создаём компанию с тем же именем
    Optional<Company> duplicate =
        companyService.addCompany(new CreateCompanyForm("Сбер", "Банкинг"));

    // Then
    assertThat(duplicate).isEmpty();
  }

  @Test
  void givenNewCompany_whenSave_thenReturnSaved() {
    // Given
    Company company = new Company("Тинькофф", "Финтех");

    // When
    Optional<Company> result = companyService.save(company);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getId()).isNotNull();
  }

  @Test
  void givenExistingName_whenSave_thenReturnEmpty() {
    // Given
    companyService.save(new Company("ВТБ", "Банки"));

    // When
    Optional<Company> result = companyService.save(new Company("ВТБ", "Финансы"));

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void givenExistingId_whenFindById_thenReturnCompany() {
    // Given
    Company saved = companyService.save(new Company("Газпром", "Энергетика")).get();

    // When
    Optional<Company> result = companyService.findById(saved.getId());

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Газпром");
  }

  @Test
  void givenNonExistentId_whenFindById_thenReturnEmpty() {
    // When
    Optional<Company> result = companyService.findById(UUID.randomUUID());

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void givenSavedCompanies_whenFindAll_thenReturnThem() {
    // Given
    companyService.save(new Company("Озон", "E-commerce"));
    companyService.save(new Company("Вайлдберриз", "E-commerce"));

    // When
    List<Company> all = companyService.findAllCompanies();

    // Then
    assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    assertThat(all).extracting(Company::getName).contains("Озон", "Вайлдберриз");
  }

  @Test
  void givenNonExistentId_whenFindByIdWithLeads_thenReturnEmpty() {
    // When
    Optional<Company> result = companyService.findByIdWithLeads(UUID.randomUUID());

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void givenCompanyWithLeads_whenDelete_thenCompanyRemovedAndLeadsDetached() {
    // Given
    Company company = companyService.save(new Company("Мегафон", "Телеком")).get();
    UUID companyId = company.getId();

    List<UUID> leadIds =
        List.of(
            leadService.addLead(new Lead("Иван", "ivan@megafon.ru", company)).get().getId(),
            leadService.addLead(new Lead("Мария", "maria@megafon.ru", company)).get().getId(),
            leadService.addLead(new Lead("Пётр", "petr@megafon.ru", company)).get().getId());

    // When
    companyService.deleteCompany(companyId);

    // Then — компании больше нет
    assertThat(companyService.findById(companyId)).isEmpty();

    // And — лиды остались, но company = null
    for (UUID leadId : leadIds) {
      Lead lead = leadService.findById(leadId).orElseThrow();
      assertThat(lead.getCompany()).isNull();
    }
  }

  @Test
  void givenCompanyWithoutLeads_whenDelete_thenCompanyRemoved() {
    // Given
    Company company = companyService.save(new Company("Ростелеком", "Телеком")).get();
    UUID companyId = company.getId();

    // When
    companyService.deleteCompany(companyId);

    // Then
    assertThat(companyService.findById(companyId)).isEmpty();
  }
}
