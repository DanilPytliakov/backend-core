package ru.mentee.power.crm.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leads")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'NEW'")
    private LeadStatus status = LeadStatus.NEW;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    @Column(name = "version", nullable = false)
    @Setter(AccessLevel.NONE)
    private Long version;

    public Lead(String name, String email, String company) {
        this(name, email, company, LeadStatus.NEW);
    }

    public Lead(String name, String email, String company, LeadStatus status) {
        this.name = name;
        this.email = email;
        this.company = company;
        this.status = status != null ? status : LeadStatus.NEW;
        this.createdAt = LocalDateTime.now();
    }
}