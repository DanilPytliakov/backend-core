## Сравнение: new внутри vs DI через конструктор
### BAD: Тесная связанность
```java
public class LeadService {
private final LeadRepository repository = new InMemoryLeadRepository();
}
```                           
### GOOD: Dependency Injection через конструктор
```java
public class LeadService {
    private final LeadRepository repository;

    public LeadService(LeadRepository repository) {
        this.repository = repository;
    }
}
