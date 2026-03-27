package ru.mentee.power.crm.service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.dto.RetryResult;

@Service
@RequiredArgsConstructor
public class LeadLockingProcessor {
    private final LeadLockingService leadLockingService;
    private static final Logger LOG = LoggerFactory.getLogger(LeadLockingProcessor.class);

    public RetryResult updateWithRetry(UUID leadId, LeadStatus newStatus) {
        int maxRetries = 3;

        for (int i = 0; i < maxRetries; i++) {
            try {
                Lead lead = leadLockingService
                        .updateLeadStatusOptimistic(leadId, newStatus);

                return new RetryResult(lead, i + 1);

            } catch (ObjectOptimisticLockingFailureException e) {
                if (i == maxRetries - 1) {
                    throw e;
                }

                try {
                    Thread.sleep((long) (Math.random() * Math.pow(2, i) * 50));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(ie);
                }
            }
        }

        throw new IllegalStateException("Unreachable");
    }
}
