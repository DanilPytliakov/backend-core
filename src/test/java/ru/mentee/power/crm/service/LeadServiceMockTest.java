package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.repository.CompanyRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@ExtendWith(MockitoExtension.class)
class LeadServiceMockTest {

  @Mock private LeadRepository mockRepository;
  @Mock private CompanyRepository companyRepository;

  @InjectMocks private LeadService service;

  private Company company;

  @BeforeEach
  void setUp() {
    company = new Company("FirstCompany", "business");
  }

  @Test
  void shouldCallRepositorySave_whenAddingNewLead() {
    // Given: Repository возвращает пустой Optional (email уникален)
    when(mockRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // When: настраиваем save чтобы возвращал переданный Lead
    when(mockRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When: вызываем бизнес-метод
    Lead result = service.addLead("Danil", "new@example.com", company).get();

    // Then: проверяем что Repository.save() был вызван ровно 1 раз
    verify(mockRepository, times(1)).save(any(Lead.class));

    // Then: проверяем результат
    assertThat(result.getEmail()).isEqualTo("new@example.com");
  }

  @Test
  void shouldNotCallSave_whenEmailExists() {
    // Given: Repository возвращает существующий Lead
    Lead existingLead = new Lead("Danil", "existing@example.com", company);
    when(mockRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingLead));

    // When/Then: ожидаем пустой лид
    assertThat(service.addLead("Danil", "existing@example.com", company).isPresent()).isFalse();

    // Then: save() НЕ должен быть вызван
    verify(mockRepository, never()).save(any(Lead.class));
  }

  @Test
  void shouldCallFindByEmail_beforeSave() {
    // Given
    when(mockRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(mockRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    service.addLead("Danil", "test@example.com", company);

    // Then: проверяем порядок вызовов
    var inOrder = inOrder(mockRepository);
    inOrder.verify(mockRepository).findByEmail("test@example.com");
    inOrder.verify(mockRepository).save(any(Lead.class));
  }
}
