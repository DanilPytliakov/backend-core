package ru.mentee.power.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import ru.mentee.power.crm.web.mvc.dto.CreateCompanyForm;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@ToString(exclude = "leads")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "leads"})
@Entity
@Table(name = "companies")
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  private String industry;

  @OneToMany(mappedBy = "company", cascade = CascadeType.PERSIST)
  private List<Lead> leads = new ArrayList<>();

  public void addLead(Lead lead) {
    leads.add(lead);
    lead.setCompany(this);
  }

  public void removeLead(Lead lead) {
    leads.remove(lead);
    lead.setCompany(null);
  }

  public Company(CreateCompanyForm form) {
    this.name = form.getName();
    this.industry = form.getIndustry();
  }

  public Company(String name, String industry) {
    this.name = name;
    this.industry = industry;
  }
}
