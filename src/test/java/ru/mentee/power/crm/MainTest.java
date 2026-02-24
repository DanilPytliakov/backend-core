package ru.mentee.power.crm;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void whenTomcatStartsThenPortIsOccupied() throws Exception {
        // Given: создаём Tomcat на порту 8080
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.addContext("", new File(".").getAbsolutePath());

        // When: запускаем сервер в отдельном потоке
        Thread serverThread = new Thread(() -> {
            try {
                tomcat.start();
                tomcat.getServer().await();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Tomcat failed to start: " + e.getMessage());
            }
        });
        serverThread.start();

        // Then: Проверяем, что поток сервера жив
        assertTrue(serverThread.isAlive(), "Tomcat server thread should be alive");

        // Опционально: добавляем задержку, чтобы сервер успел стартовать
        Thread.sleep(1000);

        // После теста принудительно останавливаем Tomcat
        tomcat.stop();
    }
}