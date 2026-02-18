package ru.netology.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class Server {

    private final Map<String, HttpHandler> handlers = new HashMap<>();

    public void addHandler(String path, HttpHandler handler) {
        handlers.put(path, handler);
    }

    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> {
            String fullPath = exchange.getRequestURI().toString();
            Request request = new Request(exchange.getRequestMethod(), fullPath);

            HttpHandler handler = handlers.get(request.getPath());
            if (handler != null) {
                handler.handle(exchange);
            } else {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();

        server.addHandler("/messages", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            Request request = new Request(exchange.getRequestMethod(),
                    exchange.getRequestURI().toString());

            String last = request.getQueryParam("last");
            String response = "Messages, last=" + (last != null ? last : "all");

            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        server.start(8080);
    }
}
