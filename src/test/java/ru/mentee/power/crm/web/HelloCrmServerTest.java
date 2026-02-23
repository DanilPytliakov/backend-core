package ru.mentee.power.crm.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HelloCrmServerTest {

    private HelloCrmServer server;
    private final int port = 8080;

    @BeforeEach
    void setUp() throws IOException {
        server = new HelloCrmServer(port);
        server.start(port);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void shouldReturn200ForHello() throws Exception {
        // Given: клиент готов отправить запрос
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/hello"))
                .GET()
                .build();

        // When: клиент отправляет GET /hello
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        // Then: ответ 200
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void shouldReturn404ForUnknownPath() throws Exception {
        // Given
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/unknown"))
                .GET()
                .build();

        // When
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        // Then
        assertThat(response.statusCode()).isEqualTo(404);
    }
}