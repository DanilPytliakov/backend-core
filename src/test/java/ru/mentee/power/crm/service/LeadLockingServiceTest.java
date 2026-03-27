package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;
import java.util.concurrent.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.dto.RetryResult;
import ru.mentee.power.crm.repository.LeadRepository;

@SpringBootTest
class LeadLockingServiceTest {

    @Autowired
    private LeadLockingService leadLockingService;

    @Autowired
    private LeadLockingProcessor leadLockingProcessor;

    @Autowired
    private LeadRepository leadRepository;

    @Test
    void shouldPreventLostUpdate_whenPessimisticLockUsed() throws Exception {
        // Given: Lead с начальным статусом
        Lead lead = new Lead("Danil", "concurrent@test.com", "Company");
        lead = leadRepository.save(lead);
        UUID leadId = lead.getId();

        // When: Два потока одновременно обновляют Lead с pessimistic lock
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        Future<LeadStatus> task1 = executor.submit(() -> {
            startLatch.await(); // Синхронизируем старт
            Lead updated = leadLockingService.convertLeadToDealWithLock(leadId, LeadStatus.CONTACTED);
            doneLatch.countDown();
            return updated.getStatus();
        });

        Future<LeadStatus> task2 = executor.submit(() -> {
            startLatch.await();
            Lead updated = leadLockingService.convertLeadToDealWithLock(leadId, LeadStatus.QUALIFIED);
            doneLatch.countDown();
            return updated.getStatus();
        });

        startLatch.countDown(); // Запускаем оба потока одновременно
        doneLatch.await(10, TimeUnit.SECONDS); // Ждём завершения

        // Then: Оба обновления успешны, вторая транзакция ждала первую
        LeadStatus status1 = task1.get();
        LeadStatus status2 = task2.get();

        assertThat(status1).isIn(LeadStatus.CONTACTED, LeadStatus.QUALIFIED);
        assertThat(status2).isIn(LeadStatus.CONTACTED, LeadStatus.QUALIFIED);
        assertThat(status1).isNotEqualTo(status2); // Разные статусы (не должны быть)

        // Финальный статус — последняя commit'нутая транзакция
        Lead finalLead = leadRepository.findById(leadId).orElseThrow();
        assertThat(finalLead.getStatus()).isIn(LeadStatus.CONTACTED, LeadStatus.QUALIFIED);

        executor.shutdown();
    }

    @Test
    void shouldThrowOptimisticLockException_whenConcurrentUpdateWithoutLock() throws Exception {
        // Given: Lead с optimistic locking через @Version
        Lead lead = new Lead("Danil", "optimistic@test.com", "Company");
        lead = leadRepository.save(lead);
        UUID leadId = lead.getId();

        // When: Два потока одновременно обновляют БЕЗ pessimistic lock
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch startLatch = new CountDownLatch(1);

        Future<?> task1 = executor.submit(() -> {
            startLatch.await();
            leadLockingService.updateLeadStatusOptimistic(leadId, LeadStatus.CONTACTED);
            return null;
        });

        Future<?> task2 = executor.submit(() -> {
            startLatch.await();
            Thread.sleep(50); // Небольшая задержка чтобы первая транзакция стартовала
            leadLockingService.updateLeadStatusOptimistic(leadId, LeadStatus.QUALIFIED);
            return null;
        });

        startLatch.countDown();

        // Then: Одна транзакция успешна, вторая выбрасывает OptimisticLockException
        boolean exceptionThrown = false;
        try {
            task1.get(5, TimeUnit.SECONDS);
            task2.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            // Одна из транзакций должна выбросить OptimisticLockException
            assertThat(e.getCause())
                    .isInstanceOfAny(ObjectOptimisticLockingFailureException.class);
            exceptionThrown = true;
        }

        assertThat(exceptionThrown).isTrue();
        executor.shutdown();
    }

    @Test
    void shouldRetryTransactionwhenConcurrentUpdateWithOptimisticLock() throws Exception {
        Lead lead = new Lead("Danil", "retry@test.com", "Company");
        lead = leadRepository.save(lead);
        UUID leadId = lead.getId();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch startLatch = new CountDownLatch(1);

        Future<RetryResult> task1 = executor.submit(() -> {
            startLatch.await();
            return leadLockingProcessor.updateWithRetry(leadId, LeadStatus.CONTACTED);
        });

        Future<RetryResult> task2 = executor.submit(() -> {
            startLatch.await();
            Thread.sleep(20); // создаём конфликт
            return leadLockingProcessor.updateWithRetry(leadId, LeadStatus.QUALIFIED);
        });

        startLatch.countDown(); // Запускаем оба потока одновременно

        RetryResult result1 = task1.get();
        RetryResult result2 = task2.get();

        // 1. Обе транзакции успешны
        assertThat(result1.lead().getStatus())
                .isIn(LeadStatus.CONTACTED, LeadStatus.QUALIFIED);

        assertThat(result2.lead().getStatus())
                .isIn(LeadStatus.CONTACTED, LeadStatus.QUALIFIED);


        // 2. Хоть у одной был retry
        assertThat(result1.attempts() > 1 || result2.attempts() > 1)
                .isTrue();

        // 3. Никто не превысил лимит
        assertThat(result1.attempts()).isBetween(1, 3);
        assertThat(result2.attempts()).isBetween(1, 3);

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }

}