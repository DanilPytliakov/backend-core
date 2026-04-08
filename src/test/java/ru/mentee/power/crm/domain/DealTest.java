package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DealTest {

  @Test
  void shouldCreateDeal_withNewStatus() {
    UUID leadId = UUID.randomUUID();
    BigDecimal amount = new BigDecimal("100000.00");

    Deal deal = new Deal(leadId, amount);
    assertThat(deal.getAmount()).isEqualTo(amount);
    assertThat(deal.getStatus()).isEqualTo(DealStatus.NEW);
    assertThat(deal.getCreatedAt()).isNotNull();
  }

  @Test
  void shouldTransitionToValidStatus() {
    Deal deal = new Deal(UUID.randomUUID(), new BigDecimal("100000.00"));

    deal.transitionTo(DealStatus.QUALIFIED);

    assertThat(deal.getStatus()).isEqualTo(DealStatus.QUALIFIED);
  }

  @Test
  void shouldThrowException_whenTransitionInvalid() {
    Deal deal = new Deal(UUID.randomUUID(), new BigDecimal("100000.00"));
    deal.transitionTo(DealStatus.QUALIFIED);
    deal.transitionTo(DealStatus.PROPOSAL_SENT);
    deal.transitionTo(DealStatus.NEGOTIATION);
    deal.transitionTo(DealStatus.WON);

    assertThatThrownBy(() -> deal.transitionTo(DealStatus.NEW))
        .isInstanceOf(IllegalStateException.class);
  }
}
