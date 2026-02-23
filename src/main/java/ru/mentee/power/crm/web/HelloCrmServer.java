package ru.mentee.power.crm.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HelloCrmServer {

    private final HttpServer server;

    public HelloCrmServer(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(null);
    }

    public void start(int port) {
        server.createContext("/hello", new HelloHandler());
        server.start();
        System.out.println("Server started on http://localhost:" + port);
    }

    public void stop() {
        server.stop(0);
    }

    static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<h1>Hello CRM!</h1>";

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }

            exchange.close();
        }
    }
}