package ru.mentee.power.crm.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
@RequiredArgsConstructor
public class LeadLockingService {

  private final LeadRepository leadRepository;
  private static final Logger LOG = LoggerFactory.getLogger(LeadLockingService.class);

  // Критическая операция с pessimistic lock
  @Transactional
  public Lead convertLeadToDealWithLock(UUID leadId, LeadStatus newStatus) {
    // Блокируем Lead эксклюзивно до конца транзакции
    Lead lead =
        leadRepository
            .findByIdForUpdate(leadId)
            .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

    // Здесь могла бы быть сложная бизнес-логика конверсии:
    // - создание Deal
    // - обновление статуса Lead
    // - отправка уведомлений
    // Другие транзакции ЖДУТ завершения этой операции

    lead.setStatus(newStatus);
    return leadRepository.save(lead);
  }

  // Обычное обновление с optimistic lock (через @Version)
  @Transactional
  public Lead updateLeadStatusOptimistic(UUID leadId, LeadStatus newStatus) {
    Lead lead =
        leadRepository
            .findById(leadId)
            .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

    // Блокировки НЕТ — другие транзакции могут читать и изменять
    // При сохранении JPA проверит version и выбросит OptimisticLockException если конфликт

    // окно для тестов
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    lead.setStatus(newStatus);
    return leadRepository.save(lead);
    // UPDATE leads SET status=?, version=version+1 WHERE id=? AND version=?
  }
}
