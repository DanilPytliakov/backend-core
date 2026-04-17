package ru.mentee.power.crm.web.rest.dto;

import java.util.UUID;

public record InviteeResponse(UUID id, String email, String firstName, String status) {}
