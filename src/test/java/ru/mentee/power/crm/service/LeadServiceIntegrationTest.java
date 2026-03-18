package ru.mentee.power.crm.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@SpringBootTest
class LeadServiceIntegrationTest {
    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private LeadService leadService;

    @Autowired
    private LeadProcessor leadProcessor;

    @BeforeEach
    void setUp() {
        leadRepository.deleteAll();
    }

    @Test
    void convertLeadToDeal_shouldRollbackOnConstraintViolation() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            leadService.convertLeadToDeal(UUID.randomUUID(), BigDecimal.valueOf(10000));
        });
        assertThat(exception.getMessage()).contains("Лид не найден");
    }

    @Test
    void required_shouldJoinExistingTransaction() {
        // REQUIRED — присоединяется к существующей транзакции
        // если транзакция уже есть — использует её
        Lead lead = leadRepository.save(new Lead("Test", "t@t.com", "Corp"));
        leadService.processLeads(List.of(lead.getId()));
        assertThat(leadRepository.findById(lead.getId()).get().getStatus())
                .isEqualTo(LeadStatus.CONTACTED);
    }

    @Test
    void requiresNew_shouldCommitIndependently() {
        // REQUIRES_NEW — каждый processSingleLead коммитится отдельно
        // даже если родительская транзакция упадёт — успешные уже закоммичены
        List<UUID> ids = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Lead saved = leadService.addLead(
                    new Lead("Lead " + i, "lead" + i + "@test.com", "Corp")).get();
            ids.add(saved.getId());
        }
        ids.add(UUID.randomUUID()); // вызовет исключение

        assertThrows(IllegalStateException.class, () -> leadService.processLeads(ids));

        // первые 3 лида уже закоммичены несмотря на исключение
        assertThat(leadRepository.countByStatus(LeadStatus.CONTACTED)).isEqualTo(3);
    }

    @Test
    void mandatory_shouldThrowException_whenNoTransaction() {
        // MANDATORY — требует существующей транзакции
        // без транзакции бросает IllegalTransactionStateException
        assertThrows(IllegalTransactionStateException.class, () ->
                leadProcessor.processSingleLeadMandatory(UUID.randomUUID())
        );
    }

    @Test
    @Transactional(isolation = Isolation.READ_COMMITTED)
    void readCommitted_shouldNotSeeUncommittedData() {
        // READ_COMMITTED — видит только закоммиченные данные
        // предотвращает грязное чтение (dirty read)
        Lead lead = leadRepository.save(new Lead("Test", "rc@test.com", "Corp"));
        Optional<Lead> found = leadRepository.findByEmail("rc@test.com");
        assertThat(found).isPresent(); // данные закоммичены — видим их
    }

    @Test
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    void repeatableRead_shouldReturnSameDataOnMultipleReads() {
        // REPEATABLE_READ — повторное чтение возвращает те же данные
        // предотвращает non-repeatable read
        Lead lead = leadRepository.save(new Lead("Test", "rr@test.com", "Corp"));

        // первое чтение
        Lead first = leadRepository.findByEmail("rr@test.com").get();

        // второе чтение в той же транзакции — данные те же
        Lead second = leadRepository.findByEmail("rr@test.com").get();

        assertThat(first.getStatus()).isEqualTo(second.getStatus());
    }
}