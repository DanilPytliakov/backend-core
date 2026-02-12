package ru.mentee.power.crm.storage;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryLeadRepositoryTest {
    /* Given пустой InMemoryLeadRepository
    When добавляю лида firstLead через add(firstLead)
    Then findAll() возвращает список из одного элемента, findById(firstLead.getId()) возвращает Optional.of(firstLead)
    */
    @Test
    void shouldReturnAddedLeadAndHisLength() {
        // Given
        InMemoryLeadRepository rep = new InMemoryLeadRepository();
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact leadContact = new Contact("test@example.com", "+71234567890", address);
        Lead lead = new Lead(leadContact, "TechCorp", "NEW");

        // When
        rep.add(lead);

        // Then
        assertThat(rep.findAll().size()).isEqualTo(1);
        assertThat(rep.findById(lead.id())).isEqualTo(Optional.of(lead));
    }

    /*
    Given InMemoryLeadRepository с 10 лидами (id от 1 до 10)
    When вызываю findById для UUID не существующего лида
    Then получаю Optional.empty()

    Тут рассмотрен фактически этот же сценарий, просто без добавления 10 элементов,
    ведь если не указывать id, то он автоматически сгенерируется конструктором при помощи
    UUID.randomUUID() и гарантированно будет уникальным
    */
    @Test
    void shouldReturnOptionalEmptyWhenTryToFindLeadByNonExistingId() {
        // Given
        InMemoryLeadRepository rep = new InMemoryLeadRepository();
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact leadContact = new Contact("test@example.com", "+71234567890", address);
        Lead lead = new Lead(leadContact, "TechCorp", "NEW");

        // When
        rep.add(lead);

        // Then
        assertThat(rep.findById(UUID.randomUUID())).isEqualTo(Optional.empty());
    }

    /* Given InMemoryLeadRepository с лидом john@example.com (UUID: abc-123)
    When пытаюсь добавить второго лида с тем же UUID через add()
    Then дубликат отклонен через contains() проверку, size() остается 1
    */
    @Test
    void shouldtAddLeadsWithSameId() {
        // Given
        InMemoryLeadRepository rep = new InMemoryLeadRepository();
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact firstLeadContact = new Contact("test@example.com", "+71234567890", address);
        Contact secondLeadContact = new Contact("test@mail.ru", "+71298765432", address);
        Lead firstLead = new Lead(firstLeadContact, "TechCorp", "NEW");
        Lead secondLead = new Lead(firstLead.id(), secondLeadContact, "AnotherCorp", "QUALIFIED");

        // When
        rep.add(firstLead);
        rep.add(secondLead);

        // Then
        assertThat(rep.findAll().size()).isEqualTo(1);
    }

    /*
    Given InMemoryLeadRepository с 5 лидами
    When вызываю remove(uuid) для существующего лида
    Then findAll() возвращает 4 лида, findById(uuid) возвращает Optional.empty()

    Сократил количество лидов до двух за неимением смысла
    */
    @Test
    void removedLeadshouldtBeeFindedBy() {
        // Given
        InMemoryLeadRepository rep = new InMemoryLeadRepository();
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact firstLeadContact = new Contact("test@example.com", "+71234567890", address);
        Contact secondLeadContact = new Contact("test@mail.ru", "+71298765432", address);
        Lead firstLead = new Lead(firstLeadContact, "TechCorp", "NEW");
        Lead secondLead = new Lead(secondLeadContact, "AnotherCorp", "QUALIFIED");

        // When
        rep.add(firstLead);
        rep.add(secondLead);

        // Then
        assertThat(rep.findAll().size()).isEqualTo(2);
        rep.remove(secondLead.id());
        assertThat(rep.findAll().size()).isEqualTo(1);
    }

    /*
    Given InMemoryLeadRepository с лидами
    When клиент вызывает findAll() и пытается изменить возвращенный список
    Then изменения не влияют на internal storage (defensive copy)
    */
    @Test
    void weShouldNotBeAbleToChangeInternalStorageManipulatingWithRerurnOfMethod() {
        // Given
        InMemoryLeadRepository rep = new InMemoryLeadRepository();
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact firstLeadContact = new Contact("test@example.com", "+71234567890", address);
        Contact secondLeadContact = new Contact("test@mail.ru", "+71298765432", address);
        Lead firstLead = new Lead(firstLeadContact, "TechCorp", "NEW");
        Lead secondLead = new Lead(secondLeadContact, "AnotherCorp", "QUALIFIED");

        // Then
        rep.add(firstLead);
        assertThat(rep.findAll().size()).isEqualTo(1);
        rep.findAll().add(secondLead);
        assertThat(rep.findAll().size()).isEqualTo(1);
    }
}