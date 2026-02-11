package ru.mentee.power.crm.domain;

import java.util.UUID;

public record Lead (
    UUID id,
    String email,
    String phone,
    String company,
    String status
) { };
