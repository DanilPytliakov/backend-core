package ru.mentee.power.crm.web.rest.config;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.stereotype.Component;

/** Регистрирует поддержку JsonNullable для generated OpenAPI DTO. */
@Component
public class OpenApiJsonNullableModule extends JsonNullableModule {

}
