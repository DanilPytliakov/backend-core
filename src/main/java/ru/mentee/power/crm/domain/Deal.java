package ru.mentee.power.crm.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deals")
@Data
@NoArgsConstructor
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "lead_id", nullable = false, updatable = false)
    private UUID leadId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false, length = 50)
    private DealStatus status = DealStatus.NEW;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DealProduct> dealProducts = new ArrayList<>();

    public Deal(UUID leadId, BigDecimal amount) {
        this.leadId = Objects.requireNonNull(leadId);
        this.amount = Objects.requireNonNull(amount);
        this.createdAt = LocalDateTime.now();
    }

    public void transitionTo(DealStatus newStatus) {
        if (status.canTransitionTo(newStatus)) {
            status = newStatus;
        } else {
            throw new IllegalStateException(
                    "Невозможно сменить статус с " + status + " на " + newStatus);
        }
    }

    public void addDealProduct(DealProduct dealProduct) {
        if (dealProduct == null) {
            throw new IllegalArgumentException("DealProduct cannot be null");
        }
        dealProducts.add(dealProduct);
        dealProduct.setDeal(this);
    }

    public void removeDealProduct(DealProduct dealProduct) {
        if (dealProduct == null) {
            throw new IllegalArgumentException("DealProduct cannot be null");
        }
        dealProducts.remove(dealProduct);
        dealProduct.setDeal(null);
    }
}