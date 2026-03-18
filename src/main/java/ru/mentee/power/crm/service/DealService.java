package ru.mentee.power.crm.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.repository.DealRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
@RequiredArgsConstructor
public class DealService {
    private final DealRepository dealRepository;
    private final LeadRepository leadRepository;

    public Deal convertLeadToDeal(UUID leadId, BigDecimal amount) {
        leadRepository.findById(leadId)
                .orElseThrow(() -> new IllegalStateException("Лид с данным id не найден: " + leadId));
        Deal deal = new Deal(leadId, amount);
        dealRepository.save(deal);
        return deal;
    }

    public Deal transitionDealStatus(UUID dealId, DealStatus newStatus) {
        Deal deal = dealRepository.findById(dealId)
                .orElseThrow(()
                        -> new IllegalArgumentException("Сделка с текущим id не найдена: " + dealId));

        try {
            deal.transitionTo(newStatus);
            dealRepository.save(deal);
            return deal;
        } catch (IllegalStateException e) {
            throw e;
        }
    }

    public List<Deal> getAllDeals() {
        return dealRepository.findAll();
    }

    public Map<DealStatus, List<Deal>> getDealsByStatusForKanban() {
        Map<DealStatus, List<Deal>> result = dealRepository.findAll().stream()
                .collect(Collectors.groupingBy(Deal::getStatus));

        for (DealStatus status : DealStatus.values()) {
            result.putIfAbsent(status, new ArrayList<>());
        }
        return result;
    }

    public Optional<Deal> findById(UUID id) {
        return dealRepository.findById(id);
    }
}