package ru.mentee.power.crm.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    @EntityGraph(attributePaths = {"leads"})
    @Query("SELECT c FROM Company c WHERE c.id = :id")
    Optional<Company> findByIdWithLeads(@Param("id") UUID id);

    // метод для поиска компании по названию
    Optional<Company> findByName(String name);

    // Метод для создания списка компаний с сортировкой по их имени
    @Query("SELECT c FROM Company c ORDER BY c.name ASC")
    List<Company> findAllCompanies();

    @Query("SELECT DISTINCT c.industry FROM Company c WHERE c.industry IS NOT NULL ORDER BY c.industry")
    List<String> findAllIndustries();
}
