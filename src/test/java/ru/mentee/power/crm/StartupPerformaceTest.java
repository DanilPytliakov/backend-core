package ru.mentee.power.crm;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.servlet.LeadListServlet;

// Второй тест был вынесен в отдельный класс,
// так как ситуации проверяемые в обоих случаях являются взаимоисключающими
class StartupPerformaceTest {
    @Test
    @DisplayName("Измерение времени старта обоих стеков")
    void shouldMeasureStartupTime() {
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        LeadService leadService = new LeadService(repository);
        for (int i = 0; i < 5; i++) {
            leadService.addLead(
                    "User" + i + "@gmail.com",
                    "Company" + i,
                    LeadStatus.NEW);
        }

        // Servlet startup time (уже запущен вручную)
        long servletStartupMs = measureServletStartup();

        // Spring Boot startup time (уже запущен вручную)
        long springStartupMs = measureSpringBootStartup();

        // Вывод результатов
        System.out.println("=== Сравнение времени старта ===");
        System.out.printf("Servlet стек: %d ms%n", servletStartupMs);
        System.out.printf("Spring Boot: %d ms%n", springStartupMs);
        System.out.printf("Разница: Spring %s на %d ms%n",
                springStartupMs > servletStartupMs ? "медленнее" : "быстрее",
                Math.abs(springStartupMs - servletStartupMs));

        // Просто фиксируем что оба стартуют за разумное время
        assertThat(servletStartupMs).isLessThan(10_000);
        assertThat(springStartupMs).isLessThan(15_000);
    }

    private long measureServletStartup() {
        try {
            long startTime = System.nanoTime();

            Tomcat tomcat = new Tomcat();
            tomcat.setPort(8080);
            tomcat.getConnector();

            // Контекст без данных лидов
            String docBase = System.getProperty("java.io.tmpdir");
            Context context = tomcat.addContext("", docBase);

            Tomcat.addServlet(context, "LeadListServlet", new LeadListServlet());
            context.addServletMappingDecoded("/leads", "LeadListServlet");

            tomcat.start();

            long endTime = System.nanoTime();

            tomcat.stop();
            tomcat.destroy();

            return (endTime - startTime) / 1_000_000; // в миллисекунды
        } catch (LifecycleException e) {
            throw new RuntimeException("Tomcat failed to start", e);
        }
    }

    private long measureSpringBootStartup() {
        long startTime = System.nanoTime();

        SpringApplication app = new SpringApplication(Application.class);
        ConfigurableApplicationContext context = app.run();

        long endTime = System.nanoTime();

        context.close(); // закрываем контекст

        return (endTime - startTime) / 1_000_000; // в миллисекунды
    }
}