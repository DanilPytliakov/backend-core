package ru.mentee.power.crm.web.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateInviteeStatusRequest(@NotBlank String status) {}
