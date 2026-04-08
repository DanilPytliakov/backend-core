package ru.mentee.power.crm.repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID>, JpaSpecificationExecutor<Lead> {

  // Обычный метод без блокировки (использует optimistic через @Version)
  Optional<Lead> findByEmail(String email);

  // Pessimistic lock для критических операций (конверсия Lead→Deal)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT l FROM Lead l WHERE l.id = :id")
  Optional<Lead> findByIdForUpdate(@Param("id") UUID id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT l FROM Lead l WHERE l.email = :email")
  Optional<Lead> findByEmailForUpdate(@Param("email") String email);

  // Поиск лида по статусу
  List<Lead> findByStatus(LeadStatus status);

  @Modifying
  @Query("UPDATE Lead l SET l.company = null WHERE l.company.id = :companyId")
  void detachFromCompany(@Param("companyId") UUID companyId);

  // Подсчёт лидов с заданным статусом
  long countByStatus(LeadStatus status);

  // Проверка существования лида с заданной почтой
  boolean existsByEmail(String email);

  // Поиск лида по email (частичное совпадение)
  List<Lead> findByEmailContaining(String emailPart);

  // Поиск лида по статусу и названию компании
  List<Lead> findByStatusAndCompanyName(LeadStatus status, String companyName);

  // Поиск лида по статусу и компании
  List<Lead> findByStatusAndCompany(LeadStatus status, Company company);

  // поиск с сортировкой по моменту создания в порядке убывания
  List<Lead> findByStatusOrderByCreatedAtDesc(LeadStatus status);

  // Поиск лидов по списку статусов (JPQL).
  @Query("SELECT l FROM Lead l WHERE l.status IN :statuses")
  List<Lead> findByStatusIn(@Param("statuses") List<LeadStatus> statuses);

  // - поиск лидов созданных после определённой даты
  @Query("SELECT l FROM Lead l WHERE l.createdAt > :date")
  List<Lead> findCreatedAfter(@Param("date") LocalDateTime date);

  // Поиск лидов относящихся к данной компании и сортировка их в порядке убывания по дате создания
  @Query("SELECT l FROM Lead l WHERE l.company = :company ORDER BY l.createdAt DESC")
  List<Lead> findByCompanyOrderedByDate(@Param("company") String company);

  // Методы с пагинацией

  // Поиск всех лидов с пагинацией (переопределяем из JpaRepository).
  Page<Lead> findAll(Pageable pageable);

  // Поиск по статусу с пагинацией (derived method).
  Page<Lead> findByStatus(LeadStatus status, Pageable pageable);

  // Поиск по названию компании с пагинацией (derived method).
  List<Lead> findByCompanyId(UUID companyId);

  // JPQL запрос с пагинацией.
  @Query("SELECT l FROM Lead l WHERE l.status IN :statuses")
  Page<Lead> findByStatusInPaged(@Param("statuses") List<LeadStatus> statuses, Pageable pageable);

  // Массовое обновление статуса лидов.
  @Modifying(clearAutomatically = true)
  @Query("UPDATE Lead l SET l.status = :newStatus WHERE l.status = :oldStatus")
  int updateStatusBulk(
      @Param("oldStatus") LeadStatus oldStatus, @Param("newStatus") LeadStatus newStatus);

  // массовое удаление по статусу:
  @Modifying
  @Query("DELETE FROM Lead l WHERE l.status = :status")
  int deleteByStatusBulk(@Param("status") LeadStatus status);

  @Modifying
  @Query("UPDATE Lead l SET l.status = :status WHERE l.id = :id")
  void updateStatus(@Param("id") UUID id, @Param("status") LeadStatus status);
}
