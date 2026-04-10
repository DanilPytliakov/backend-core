package ru.mentee.power.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "leads")
@Data
@ToString(exclude = "company")
@EqualsAndHashCode(of = "email")
@NoArgsConstructor
@AllArgsConstructor
public class Lead {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  @JsonIgnoreProperties({"leads"})
  private Company company;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'NEW'")
  private LeadStatus status = LeadStatus.NEW;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Version
  @Column(name = "version", nullable = false)
  @Setter(AccessLevel.NONE)
  private Long version;

  public Lead(String name, String email, Company company) {
    this(name, email, company, LeadStatus.NEW);
  }

  public Lead(String name, String email, Company company, LeadStatus status) {
    this.name = name;
    this.email = email;
    this.company = company;
    this.status = status != null ? status : LeadStatus.NEW;
    this.createdAt = LocalDateTime.now();
  }
}
