package ru.mentee.power.crm.dto;

import ru.mentee.power.crm.domain.Lead;

public record RetryResult(Lead lead, int attempts) {}
