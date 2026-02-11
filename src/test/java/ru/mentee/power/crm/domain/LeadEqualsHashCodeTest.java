package ru.mentee.power.crm.domain;

class LeadEqualsHashCodeTest {
    /*
    //Рефлексивность
    @Test
    void shouldBeReflexiveWhenEqualsCalledOnSameObject() {
        // Given
        Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");

        // Then
        assertThat(lead.equals(lead)).isEqualTo(true);
    }

    //Симметричность
    @Test
    void shouldBeSymmetricWhenEqualsCalledOnTwoObjects() {
        // Given
        Lead firstLead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");
        Lead secondLead = new Lead(firstLead.id(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");

        // Then
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(secondLead).isEqualTo(firstLead);
    }

    //Транзитивность
    @Test
    void shouldBeTransitiveWhenEqualsChainOfThreeObjects() {
        // Given
        Lead firstLead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");
        Lead secondLead = new Lead(firstLead.id(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");
        Lead thirdLead = new Lead(firstLead.id(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");

        // Then
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(secondLead).isEqualTo(thirdLead);
        assertThat(firstLead).isEqualTo(thirdLead);
    }

    @Test
    void shouldBeConsistentWhenEqualsCalledMultipleTimes() {
        // Given
        Lead firstLead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");
        Lead secondLead = new Lead(firstLead.id(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");

        // Then: Результат одинаковый при многократных вызовах
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(firstLead).isEqualTo(secondLead);
    }

    //Консистентность
    @Test
    void shouldReturnFalseWhenEqualsComparedWithNull() {
        // Given
        Lead lead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");

        // Then: Объект не равен null (isNotEqualTo проверяет equals(null) = false)
        assertThat(lead).isNotEqualTo(null);
    }

    //null-безопасность
    @Test
    void shouldBeNullSafeWhenEqualsCalledOnVariableWithNullInstedOfObjectReference() {
        // Given

        Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");
        Lead nullLead = null;

        // Then
        assertThat(lead.equals(nullLead)).isEqualTo(false);
    }

    //Objects.equals(id, lead.id) — null-безопасное сравнение
    @Test
    void shouldBeObjectEqualsNullSafes() {
        // Given
        Lead firstLead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");
        Lead secondLead = new Lead(null, "test@example.com", "+71234567890", "TestCorp", "NEW");
        Lead thirdLead = new Lead(null, "test@example.com", "+71234567890", "TestCorp", "NEW");

        //Первый null, второй нет (email == null && lead.email == "...")
        assertThat(secondLead.equals(firstLead)).isEqualTo(false);
        //Второй null, первый нет (email == null && lead.email == "...")
        assertThat(firstLead.equals(secondLead)).isEqualTo(false);
        //Оба null (email == null && lead.email == null)
        assertThat(secondLead.equals(thirdLead)).isEqualTo(true);

    }

    //HashMap
    @Test
    void shouldWorkInHashMapWhenLeadUsedAsKey() {
        // Given
        Lead keyLead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");
        Lead lookupLead = new Lead(keyLead.id(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");

        Map<Lead, String> map = new HashMap<>();
        map.put(keyLead, "CONTACTED");

        // When: Получаем значение по другому объекту с тем же id
        String status = map.get(lookupLead);

        // Then: HashMap нашел значение благодаря equals/hashCode
        assertThat(status).isEqualTo("CONTACTED");
    }

     */
}