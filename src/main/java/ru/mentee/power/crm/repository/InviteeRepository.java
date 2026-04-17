package ru.mentee.power.crm.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mentee.power.crm.domain.Invitee;

public interface InviteeRepository extends JpaRepository<Invitee, UUID> {
  boolean existsByEmail(@NotBlank @Email String email);
}
