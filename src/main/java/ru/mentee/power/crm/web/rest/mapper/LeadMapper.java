package ru.mentee.power.crm.web.rest.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.jackson.nullable.JsonNullable;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.spring.dto.generated.UpdateLeadRequest;

@Mapper(componentModel = "spring")
public interface LeadMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "version", ignore = true)
  Lead toEntity(CreateLeadRequest dto);

  @Mapping(target = "companyId", source = "company.id")
  LeadResponse toResponse(Lead entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "version", ignore = true)
  void updateEntity(UpdateLeadRequest dto, @MappingTarget Lead entity);

  default UUID map(JsonNullable<UUID> value) {
    if (value == null || !value.isPresent()) {
      return null;
    }
    return value.get();
  }

  default JsonNullable<UUID> map(UUID value) {
    return value == null ? JsonNullable.undefined() : JsonNullable.of(value);
  }

  default LeadStatus map(ru.mentee.power.crm.spring.dto.generated.LeadStatus status) {
    return status == null ? null : LeadStatus.valueOf(status.getValue());
  }

  default ru.mentee.power.crm.spring.dto.generated.LeadStatus map(LeadStatus status) {
    return status == null
        ? null
        : ru.mentee.power.crm.spring.dto.generated.LeadStatus.fromValue(status.name());
  }

  default OffsetDateTime map(LocalDateTime value) {
    return value == null ? null : value.atOffset(ZoneOffset.UTC);
  }
}
