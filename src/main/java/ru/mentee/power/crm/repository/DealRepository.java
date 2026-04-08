package ru.mentee.power.crm.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;

@Repository
public interface DealRepository extends JpaRepository<Deal, UUID> {

  List<Deal> findByStatus(DealStatus status);

  List<Deal> findByLeadId(UUID leadId);

  long countByStatus(DealStatus status);

  @EntityGraph(attributePaths = {"dealProducts", "dealProducts.product"})
  @Query("SELECT d FROM Deal d WHERE d.id = :id")
  Optional<Deal> findDealWithProducts(@Param("id") UUID id);
}
