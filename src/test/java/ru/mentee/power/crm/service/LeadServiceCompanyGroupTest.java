package ru.mentee.power.crm.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.CompanyGroup;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.CompanyGroupRepository;
import ru.mentee.power.crm.repository.CompanyRepository;
import ru.mentee.power.crm.repository.LeadRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LeadServiceCompanyGroupTest {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyGroupRepository companyGroupRepository;

    @Test
    void shouldUpdateAllLeadsStatusByCompanyGroup() {
        // Given: Создаём группу
        CompanyGroup group = companyGroupRepository.save(new CompanyGroup("test@group.com"));

        // Создаём компании и сразу привязываем к группе
        Company company1 = new Company("Company 1", "IT");
        company1.setCompanyGroup(group);

        Company company2 = new Company("Company 2", "Finance");
        company2.setCompanyGroup(group);

        // Сохраняем компании (группа уже в persistence context)
        company1 = companyRepository.save(company1);
        company2 = companyRepository.save(company2);

        // Важно: не вызываем companyGroupRepository.save(group) повторно!

        // Создаём лидов
        Lead lead1 = new Lead("Lead1", "lead1@test.com", company1);
        Lead lead2 = new Lead("Lead2", "lead2@test.com", company1);
        Lead lead3 = new Lead("Lead3", "lead3@test.com", company2);
        leadRepository.saveAll(java.util.List.of(lead1, lead2, lead3));

        // Принудительно синхронизируем с БД
        leadRepository.flush();

        // When: Обновляем статус всех лидов в группе
        int updatedCount = leadRepository.updateStatusByCompanyGroupEmail(group.getEmail(), LeadStatus.CONTACTED);

        // Then
        assertThat(updatedCount).isEqualTo(3);

        // Проверяем статусы
        assertThat(leadRepository.findById(lead1.getId()).get().getStatus()).isEqualTo(LeadStatus.CONTACTED);
        assertThat(leadRepository.findById(lead2.getId()).get().getStatus()).isEqualTo(LeadStatus.CONTACTED);
        assertThat(leadRepository.findById(lead3.getId()).get().getStatus()).isEqualTo(LeadStatus.CONTACTED);
    }
}