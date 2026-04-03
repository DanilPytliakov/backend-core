package ru.mentee.power.crm.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

public class LeadSpecifications {

    public static Specification<Lead> hasName(String name) {
        return (root, query, cb) ->
                name == null || name.isBlank() ? null :
                        cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Lead> hasEmail(String email) {
        return (root, query, cb) ->
                email == null || email.isBlank() ? null :
                        cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<Lead> hasCompanyName(String companyName) {
        return (root, query, cb) -> {
            if (companyName == null || companyName.isBlank()) return null;
            // Переиспользуем существующий join если он уже есть
            Join<Lead, Company> company = getOrCreateCompanyJoin(root);
            return cb.like(cb.lower(company.get("name")),
                    "%" + companyName.toLowerCase() + "%");
        };
    }

    public static Specification<Lead> hasCompanyIndustry(String industry) {
        return (root, query, cb) -> {
            if (industry == null || industry.isBlank()) return null;
            Join<Lead, Company> company = getOrCreateCompanyJoin(root);
            return cb.like(cb.lower(company.get("industry")),
                    "%" + industry.toLowerCase() + "%");
        };
    }

    // Вспомогательный метод — берёт существующий join или создаёт новый
    @SuppressWarnings("unchecked")
    private static Join<Lead, Company> getOrCreateCompanyJoin(Root<Lead> root) {
        return root.getJoins().stream()
                .filter(j -> j.getAttribute().getName().equals("company"))
                .map(j -> (Join<Lead, Company>) j)
                .findFirst()
                .orElseGet(() -> root.join("company", JoinType.LEFT));
    }

    public static Specification<Lead> hasStatus(LeadStatus status) {
        return (root, query, cb) ->
                status == null ? null :
                        cb.equal(root.get("status"), status);
    }
}