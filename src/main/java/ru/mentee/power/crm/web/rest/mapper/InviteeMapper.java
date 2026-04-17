package ru.mentee.power.crm.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.power.crm.domain.Invitee;
import ru.mentee.power.crm.web.rest.dto.CreateInviteeRequest;
import ru.mentee.power.crm.web.rest.dto.InviteeResponse;

@Mapper(componentModel = "spring")
public interface InviteeMapper {
  InviteeResponse toResponse(Invitee invitee);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "status", ignore = true)
  Invitee toEntity(CreateInviteeRequest request);
}
