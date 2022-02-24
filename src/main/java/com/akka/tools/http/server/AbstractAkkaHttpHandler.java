package com.akka.tools.http.server;

import com.akka.tools.http.model.Request;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

abstract class AbstractAkkaHttpHandler implements HttpHandler {

    String[] method;
    public abstract void init();

    @Override
    public void handle(HttpExchange exchange) {

        String reqMethod = exchange.getRequestMethod();
        if (!Arrays.asList(method).contains(reqMethod)) {
            throw new RuntimeException();
        }
        Headers requestHeaders = exchange.getRequestHeaders();
        URI requestURI = exchange.getRequestURI();
        final InputStream requestBody = exchange.getRequestBody();
        Request request = new Request();

    }
}
