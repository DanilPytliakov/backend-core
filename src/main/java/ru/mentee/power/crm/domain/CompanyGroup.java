package ru.mentee.power.crm.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "company_group")
public class CompanyGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "companyGroup", cascade = CascadeType.PERSIST)
    private List<Company> companies = new ArrayList<>();

    public CompanyGroup(String email) {
        this.email = email;
    }
}
