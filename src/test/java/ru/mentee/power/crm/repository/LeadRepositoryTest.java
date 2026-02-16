package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.model.Lead;

class LeadRepositoryTest {
    private LeadRepository repository;

    @BeforeEach
    void setUp() {
        repository = new LeadRepository();
    }

    @Test
    void shouldSaveAndFindLeadByIdWhenLeadSaved() {
        // When
        repository.save(new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW"));

        // Then
        assertThat(repository.findById("1")).isNotNull();
    }

    @Test
    void shouldReturnNullWhenLeadNotFound() {
        assertThat(repository.findById("unknown.id")).isNull();
    }

    @Test
    void shouldReturnAllLeadsWhenMultipleLeadsSaved() {
        // Given
        for (int i = 0; i < 3; i++) {
            repository.save(new Lead(String.valueOf(i), "ivan@mail.ru", "+7123", "TechCorp", "NEW"));
        }

        // Then
        assertThat(repository.findAll()).hasSize(3);
    }

    @Test
    void shouldDeleteLeadWhenLeadExists() {
        // Given
        repository.save(new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW"));

        // When
        repository.delete("1");

        // Then
        assertThat(repository.findById("1")).isNull();
        assertThat(repository.findAll()).hasSize(0);
    }

    @Test
    void shouldOverwriteLeadWhenSaveWithSameId() {
        // Given
        repository.save(new Lead("lead-1", "ivan@mail.ru", "+7123", "TechCorp", "NEW"));

        // Then
        Lead secondLead = new Lead("lead-1", "danil@mail.ru", "+7123", "TechCorp", "NEW");
        repository.save(secondLead);

        //Then
        assertThat(repository.findById("lead-1")).isEqualTo(secondLead);
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void shouldFindFasterWithMapThanWithListFilter() {
        // Given: Создать 1000 лидов
        List<Lead> leadList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Lead lead = new Lead(String.valueOf(i), i + "gmail.com", "+7123" + i, "Company" + i, "NEW");
            repository.save(lead);
            leadList.add(lead);
        }

        String targetId = "500";  // Средний элемент

        // When: Поиск через Map
        long mapStart = System.nanoTime();
        Lead foundInMap = repository.findById(targetId);
        long mapDuration = System.nanoTime() - mapStart;

        // When: Поиск через List.stream().filter()
        long listStart = System.nanoTime();
        Lead foundInList = leadList.stream()
                .filter(lead -> lead.id().equals(targetId))
                .findFirst()
                .orElse(null);
        long listDuration = System.nanoTime() - listStart;

        // Then: Map должен быть минимум в 10 раз быстрее
        assertThat(foundInMap).isEqualTo(foundInList);
        assertThat(listDuration).isGreaterThan(mapDuration * 10);

        System.out.println("Map поиск: " + mapDuration + " ns");
        System.out.println("List поиск: " + listDuration + " ns");
        System.out.println("Ускорение: " + (listDuration / mapDuration) + "x");
    }

    @Test
    void shouldSaveBothLeadsEvenWithSameEmailAndPhoneBecauseRepositoryDoesNotCheckBusinessRules() {
        // When: сохраняем оба
        repository.save(new Lead("lead-1", "ivan@mail.ru", "+7123", "TechCorp", "NEW"));
        repository.save(new Lead("lead-2", "ivan@mail.ru", "+7123", "TechCorp", "NEW"));

        // Then: Repository сохранил оба (это технически правильно!)
        assertThat(repository.size()).isEqualTo(2);

        // But: Бизнес недоволен — в CRM два контакта на одного человека
        // Решение: Service Layer в Sprint 5 будет проверять бизнес-правила
        // перед вызовом repository.save()
    }
}