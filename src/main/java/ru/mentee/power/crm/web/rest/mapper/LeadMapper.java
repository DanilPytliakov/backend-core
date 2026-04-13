package ru.mentee.power.crm.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.web.rest.dto.CreateLeadRequest;
import ru.mentee.power.crm.web.rest.dto.LeadResponse;
import ru.mentee.power.crm.web.rest.dto.UpdateLeadRequest;

@Mapper
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
}
