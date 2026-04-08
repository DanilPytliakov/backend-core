package ru.mentee.power.crm.spring.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.service.CompanyService;
import ru.mentee.power.crm.service.LeadService;

@SpringBootTest(properties = "gg.jte.template-location=src/main/jte")
@Transactional
@AutoConfigureMockMvc
class LeadControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private LeadService leadService;
  @Autowired private CompanyService companyService;
  @Autowired private EntityManager entityManager;

  // ───── showLeads ─────

  @Test
  void givenSpringContext_whenGetLeads_thenStatus200AndContainsEmail() throws Exception {
    // Given: Spring контекст загружен

    // When,Then
    mockMvc
        .perform(get("/leads"))
        // статус 200 OK
        .andExpect(status().isOk())
        // ответ содержит заголовок колонки
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Почтовый адрес")));
  }

  @Test
  void givenValidStatus_whenGetLeadsWithFilter_thenStatus200AndContainsFilter() throws Exception {
    // Given: корректный фильтр по статусу NEW

    // Когда: GET /leads?status=NEW
    mockMvc
        .perform(get("/leads").param("status", "NEW"))
        // Тогда: статус 200 OK
        .andExpect(status().isOk())
        // И: ответ содержит указание на фильтр
        .andExpect(content().string(org.hamcrest.Matchers.containsString("NEW")));
  }

  @Test
  void givenInvalidStatus_whenGetLeadsWithFilter_thenStatus400() throws Exception {
    // Given: некорректное значение статуса

    // When: GET /leads?status=INVALID
    mockMvc
        .perform(get("/leads").param("status", "INVALID"))
        // Then: статус 400 Bad Request
        .andExpect(status().isBadRequest());
  }

  // ───── showCreateForm ─────

  @Test
  void givenSpringContext_whenGetLeadsNew_thenStatus200AndContainsForm() throws Exception {
    // Given: Spring контекст загружен

    // When,Then GET /leads/new
    mockMvc
        .perform(get("/leads/new"))
        // статус 200 OK
        .andExpect(status().isOk())
        // ответ содержит элементы формы
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Добавить")));
  }

  // ───── createLead ─────

  @Test
  void givenValidLead_whenPostLeads_thenRedirectToList() throws Exception {
    // Given: корректные данные нового лида

    // When: POST /leads
    mockMvc
        .perform(
            post("/leads")
                .param("name", "Danil")
                .param("email", "test@company.com")
                .param("company", "TestCorp")
                .param("status", "NEW"))
        // Тогда: редирект на /leads
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/leads"));
  }

  @Test
  void givenDuplicateEmail_whenPostLeads_thenStayOnCreatePage() throws Exception {
    // Given: лид с таким email уже существует
    mockMvc.perform(
        post("/leads")
            .param("name", "Danil")
            .param("email", "duplicate@company.com")
            .param("company", "Corp")
            .param("status", "NEW"));

    // When: POST /leads с тем же email
    mockMvc
        .perform(
            post("/leads")
                .param("name", "Ivan")
                .param("email", "duplicate@company.com")
                .param("company", "AnotherCorp")
                .param("status", "CONTACTED"))
        // Then: остаёмся на странице создания
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Добавить")));
  }

  // ───── showLeadUpdating ─────

  @Test
  void givenNonExistentId_whenGetLeadEdit_thenShowListWithNotFoundMessage() throws Exception {
    // Дано: несуществующий UUID
    String nonExistentId = UUID.randomUUID().toString();

    // Когда: GET /leads/{id}/edit
    mockMvc
        .perform(get("/leads/" + nonExistentId + "/edit"))
        // Тогда: остаёмся на странице списка
        .andExpect(status().isOk())
        // И: отображается сообщение об ошибке
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Не удалось найти")));
  }

  @Test
  void givenExistingLead_whenGetLeadEdit_thenStatus200AndContainsForm() throws Exception {
    // Given: существующий лид
    mockMvc.perform(
        post("/leads")
            .param("name", "Danil")
            .param("email", "edit@company.com")
            .param("company", "EditCorp")
            .param("status", "NEW"));

    // When: GET /leads — проверяем что лид отображается в списке
    mockMvc
        .perform(get("/leads"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("edit@company.com")));
  }

  // ───── updateLead ─────

  @Test
  void givenExistingLead_whenPostLeadEdit_thenRedirectToList() throws Exception {
    // Given: создаём лида
    mockMvc.perform(
        post("/leads")
            .param("name", "Danil")
            .param("email", "update@company.com")
            .param("company", "UpdateCorp")
            .param("status", "NEW"));

    // Получаем реальный UUID
    UUID id = leadService.findByEmail("update@company.com").get().getId();

    // When: POST /leads/{id}/edit
    mockMvc
        .perform(
            post("/leads/" + id + "/edit")
                .param("name", "Danil")
                .param("email", "updated@company.com")
                .param("company", "UpdatedCorp")
                .param("status", "CONTACTED"))
        // Then: редирект на /leads
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/leads"));
  }

  @Test
  void shouldDeleteLeadAndRedirect() throws Exception {
    // Given: создаём лида
    mockMvc.perform(
        post("/leads")
            .param("name", "Danil")
            .param("email", "example@company.com")
            .param("company", "Corp")
            .param("status", "NEW"));

    // Получаем UUID созданного лида
    UUID id = leadService.findByEmail("example@company.com").get().getId();

    // When удаляем по реальному UUID
    mockMvc
        .perform(post("/leads/" + id + "/delete"))
        // Then: редирект на /leads
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/leads"));

    // And лид больше не отображается в списке
    mockMvc
        .perform(get("/leads"))
        .andExpect(
            content()
                .string(
                    org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("example@company.com"))));
  }

  @Test
  void givenLeads_whenFilterByEmails_thenReturnMatchingLeads() throws Exception {
    // Given: создаём лида
    mockMvc.perform(
        post("/leads")
            .param("name", "Danil")
            .param("email", "first@gmail.com")
            .param("company", "ExampleCorp")
            .param("status", "NEW"));
    mockMvc.perform(
        post("/leads")
            .param("name", "Danil")
            .param("email", "second@mail.ru")
            .param("company", "ExampleCorp")
            .param("status", "CONTACTED"));

    // When: фильтруем по подстроке "ru"
    mockMvc
        .perform(get("/leads").param("email", "ru"))
        // Then: статус 200
        .andExpect(status().isOk())
        // And: лид содержащий ru в почте отображается
        .andExpect(content().string(org.hamcrest.Matchers.containsString("second@mail.ru")))
        // And: лид не содержащий ru в почте не отображается
        .andExpect(
            content()
                .string(
                    org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("first@gmail.com"))));
  }

  @Test
  void givenLeadsWithDifferentStatuses_whenFilterByNew_thenReturnOnlyNewLeads() throws Exception {
    // Given: создаём лида
    mockMvc.perform(
        post("/leads")
            .param("name", "Danil")
            .param("email", "first@gmail.com")
            .param("company", "ExampleCorp")
            .param("status", "NEW"));
    mockMvc.perform(
        post("/leads")
            .param("name", "Danil")
            .param("email", "second@mail.ru")
            .param("company", "ExampleCorp")
            .param("status", "CONTACTED"));

    // When: фильтруем по статусу NEW
    mockMvc
        .perform(get("/leads").param("status", "NEW"))
        // Then: статус 200
        .andExpect(status().isOk())
        // And: лид со статусом NEW отображается
        .andExpect(content().string(org.hamcrest.Matchers.containsString("first@gmail.com")))
        // And: лид со статусом CONTACTED не отображается
        .andExpect(
            content()
                .string(
                    org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("second@mail.ru"))));
  }

  @Test
  void givenLeads_whenFilterByEmailAndStatus_thenReturnMatchingLeads() throws Exception {
    // Given: создаём лидов
    mockMvc.perform(
        post("/leads")
            .param("name", "Danil")
            .param("email", "first@gmail.com")
            .param("company", "ExampleCorp")
            .param("status", "NEW"));
    mockMvc.perform(
        post("/leads")
            .param("name", "Danil")
            .param("email", "second@mail.ru")
            .param("company", "ExampleCorp")
            .param("status", "CONTACTED"));
    mockMvc.perform(
        post("/leads")
            .param("name", "Danil")
            .param("email", "third@mail.ru")
            .param("company", "ExampleCorp")
            .param("status", "NEW"));

    // When: фильтруем по email содержащему "test" и статусу NEW
    mockMvc
        .perform(get("/leads").param("email", "ru").param("status", "NEW"))
        // Then: статус 200
        .andExpect(status().isOk())
        // And: лид совпадающий по обоим фильтрам отображается
        .andExpect(content().string(org.hamcrest.Matchers.containsString("third@mail.ru")))
        // And: лид с email "ru" но другим статусом не отображается
        .andExpect(
            content()
                .string(
                    org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("second@mail.ru"))))
        // And: лид со статусом NEW но другим email не отображается
        .andExpect(
            content()
                .string(
                    org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("first@gmail.com"))));
  }

  @Test
  void givenMultipleLeads_whenGetLeadsWithoutFilter_thenReturnAllLeads() throws Exception {
    // Given: создаём лида
    mockMvc.perform(
        post("/leads")
            .param("name", "Danil")
            .param("email", "first@gmail.com")
            .param("company", "ExampleCorp")
            .param("status", "NEW"));
    mockMvc.perform(
        post("/leads")
            .param("name", "Danil")
            .param("email", "second@mail.ru")
            .param("company", "ExampleCorp")
            .param("status", "CONTACTED"));

    // When: GET /leads без параметров
    mockMvc
        .perform(get("/leads"))
        // Then: статус 200
        .andExpect(status().isOk())
        // And: все лиды отображаются
        .andExpect(content().string(org.hamcrest.Matchers.containsString("first@gmail.com")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("second@mail.ru")));
  }

  @Test
  void givenInvalidEmail_whenPostLeads_thenReturnFormWithEmailError() throws Exception {
    // Given: некорректный формат email

    // When: POST /leads с невалидным email
    mockMvc
        .perform(
            post("/leads")
                .param("email", "invalidemail")
                .param("company", "TestCorp")
                .param("status", "NEW"))
        // Then: остаёмся на странице создания
        .andExpect(view().name("leads/create"))
        // And: ошибка формата email
        .andExpect(model().attributeHasFieldErrorCode("leadForm", "email", "Email"));
  }

  @Test
  void givenValidData_whenPostLeads_thenRedirectToList() throws Exception {
    // Given: все поля заполнены корректно

    // When: POST /leads с валидными данными
    mockMvc
        .perform(
            post("/leads")
                .param("name", "Danil")
                .param("email", "valid@company.com")
                .param("company", "ValidCorp")
                .param("status", "NEW"))
        // Then: редирект на /leads
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/leads"));
  }

  @Test
  void givenInvalidEmail_whenPostLeads_thenEnglishErrorMessage() throws Exception {
    mockMvc
        .perform(
            post("/leads")
                .param("email", "invalidemail")
                .param("company", "TestCorp")
                .param("status", "NEW")
                .header("Accept-Language", "en"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid email format")));
  }

  @Test
  void givenInvalidEmail_whenPostLeads_thenRussianErrorMessage() throws Exception {
    mockMvc
        .perform(
            post("/leads")
                .param("email", "invalidemail")
                .param("company", "TestCorp")
                .param("status", "NEW")
                .header("Accept-Language", "ru"))
        .andExpect(status().isOk())
        // проверяем код ошибки, а не текст сообщения
        .andExpect(model().attributeHasFieldErrorCode("leadForm", "email", "Email"));
  }

  @Test
  void shouldAvoidN1WithEntityGraph() {
    // Given — создаём компанию с 3 лидами
    Company company = new Company("Яндекс", "бигтех");
    for (int i = 0; i < 3; i++) {
      company.addLead(new Lead("lead" + i, "lead" + i + "@tinkoff.ru", company));
    }

    Company saved = companyService.save(company).get();

    // When — используем метод с @EntityGraph
    Company found = companyService.findByIdWithLeads(saved.getId()).orElseThrow();

    // Then — проверяем, что leads загружены
    assertThat(found.getLeads()).hasSize(3);

    // Проверьте SQL логи: должен быть 1 запрос с LEFT JOIN,
    // а не 1 SELECT для Company + 5 SELECT для каждого Lead
  }

  @Test
  void shouldSetNullInsteadDeletedCompany() {
    // Given — сначала сохраняем компанию
    Company company = new Company("Яндекс", "бигтех");
    Company saved = companyService.save(company).get();
    UUID companyId = saved.getId();

    // Потом создаём и сохраняем лиды отдельно
    for (int i = 0; i < 3; i++) {
      Lead lead = new Lead("lead" + i, "lead" + i + "@tinkoff.ru", saved);
      leadService.addLead(lead); // ← явное сохранение каждого лида
    }

    // Проверяем начальное состояние
    List<Lead> leadsBefore = leadService.findByCompanyId(companyId);
    assertThat(leadsBefore).hasSize(3);
    assertThat(leadsBefore.get(0).getCompany()).isNotNull();

    // When
    companyService.deleteCompany(companyId);
    entityManager.clear(); // ← сбрасываем L1 кеш

    // Then
    assertThat(companyService.findById(companyId)).isEmpty();

    // Или ищем по email которые мы создали
    List<Lead> ourLeads =
        leadService.findAll().stream().filter(l -> l.getEmail().endsWith("@tinkoff.ru")).toList();

    assertThat(ourLeads).hasSize(3);
    for (Lead lead : ourLeads) {
      assertThat(lead.getCompany()).isNull();
    }
  }
}
