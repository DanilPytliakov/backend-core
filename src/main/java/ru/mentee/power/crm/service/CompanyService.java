package ru.mentee.power.crm.service;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.repository.CompanyRepository;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.web.mvc.dto.CreateCompanyForm;

@Service
@RequiredArgsConstructor
public class CompanyService {
  private final LeadRepository leadRepository;
  private final CompanyRepository companyRepository;
  private final EntityManager entityManager;

  public Optional<Company> addCompany(CreateCompanyForm form) {
    Optional<Company> existing = companyRepository.findByName(form.getName());

    if (existing.isPresent()) {
      return Optional.empty(); // явно говорим "компания не создана"
    } else {
      Company company = new Company(form);
      return Optional.of(companyRepository.save(company));
    }
  }

  public List<Company> findAllCompanies() {
    return companyRepository.findAllCompanies();
  }

  public Optional<Company> findById(UUID id) {
    return companyRepository.findById(id);
  }

  public Optional<Company> save(Company company) {
    if (companyRepository.findByName(company.getName()).isEmpty()) {
      companyRepository.save(company);
      return Optional.of(company);
    } else {
      return Optional.empty();
    }
  }

  public Optional<Company> findByIdWithLeads(UUID id) {
    return companyRepository.findByIdWithLeads(id);
  }

  @Transactional
  public void deleteCompany(UUID companyId) {
    leadRepository.detachFromCompany(companyId);
    leadRepository.flush();
    entityManager.clear();
    entityManager
        .createQuery("DELETE FROM Company c WHERE c.id = :id")
        .setParameter("id", companyId)
        .executeUpdate();
  }

  public List<String> findAllIndustries() {
    return companyRepository.findAllIndustries();
  }
}
