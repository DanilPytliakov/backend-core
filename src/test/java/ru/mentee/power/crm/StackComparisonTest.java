package ru.mentee.power.crm;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.servlet.LeadListServlet;

/**
 * Интеграционный тест сравнения Servlet и Spring Boot стеков.
 * Запускает оба сервера, выполняет HTTP запросы, сравнивает результаты.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StackComparisonTest {

    private static final int SERVLET_PORT = 8080;
    private static final int SPRING_PORT = 8081;

    private static HttpClient httpClient;
    private static Tomcat tomcat;
    private ConfigurableApplicationContext springContext;

    @BeforeAll
     void startServers() throws Exception {
        // Поднимаем Tomcat
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        LeadService leadService = new LeadService(repository);

        tomcat = new Tomcat();
        tomcat.setPort(SERVLET_PORT);
        tomcat.getConnector();

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        ctx.getServletContext().setAttribute("leadService", leadService);

        tomcat.addServlet(ctx, "LeadListServlet", new LeadListServlet());
        ctx.addServletMappingDecoded("/leads", "LeadListServlet");

        tomcat.start();

        // Поднимаем Spring Boot
        springContext = SpringApplication.run(Application.class,
                "--server.port=" + SPRING_PORT);

        // HTTP клиент и проверка готовности
        httpClient = HttpClient.newHttpClient();

        // Ждём готовности Tomcat
        await().atMost(10, SECONDS).until(() -> {
            try {
                HttpResponse<String> resp = httpClient.send(
                        HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:" + SERVLET_PORT + "/leads"))
                                .timeout(Duration.ofSeconds(2))
                                .build(),
                        HttpResponse.BodyHandlers.ofString()
                );
                return resp.statusCode() == 200;
            } catch (IOException | InterruptedException e) {
                return false;
            }
        });

        // Ждём готовности Spring Boot
        await().atMost(10, SECONDS).until(() -> {
            try {
                HttpResponse<String> resp = httpClient.send(
                        HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:" + SPRING_PORT + "/leads"))
                                .timeout(Duration.ofSeconds(2))
                                .build(),
                        HttpResponse.BodyHandlers.ofString()
                );
                return resp.statusCode() == 200;
            } catch (IOException | InterruptedException e) {
                return false;
            }
        });
    }

    @AfterAll
    void stopServers() throws Exception {
        if (tomcat != null) {
            tomcat.stop();
            tomcat.destroy();
        }
        if (springContext != null) {
            SpringApplication.exit(springContext);
        }
    }

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
    }

    // Подсчитывает количество строк <tr> в HTML (количество лидов в таблице).
    private int countTableRows(String html) {
        if (html == null || html.isEmpty()) {
            return 0;
        }
        // Приводим к нижнему регистру, чтобы ловить <tr> и <TR>
        String[] rows = html.toLowerCase().split("<tr");
        // split возвращает массив, первый элемент до первого <tr> не считается строкой
        return Math.max(0, rows.length - 1);
    }

    @Test
    @DisplayName("Оба стека должны возвращать лидов в HTML таблице")
    void shouldReturnLeadsFromBothStacks() throws Exception {
        // Given: HTTP запросы к обоим стекам
        HttpRequest servletRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + SERVLET_PORT + "/leads"))
                .GET()
                .build();

        HttpRequest springRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + SPRING_PORT + "/leads"))
                .GET()
                .build();

        // When: выполняем запросы
        HttpResponse<String> servletResponse = httpClient.send(
                servletRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> springResponse = httpClient.send(
                springRequest, HttpResponse.BodyHandlers.ofString());

        // Then: оба возвращают 200 OK и содержат таблицу
        assertThat(servletResponse.statusCode()).isEqualTo(200);
        assertThat(springResponse.statusCode()).isEqualTo(200);

        assertThat(servletResponse.body()).contains("<table");
        assertThat(springResponse.body()).contains("<table");

        // Сравниваем количество строк <tr>
        int servletRows = countTableRows(servletResponse.body());
        int springRows = countTableRows(springResponse.body());
        assertThat(servletRows).isEqualTo(springRows);
    }
}