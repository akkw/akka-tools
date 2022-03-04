package com.akka.tools.http.server;

import com.akka.tools.http.model.AkkaHttpHeader;
import com.akka.tools.http.model.AkkaHttpHeader.*;
import com.akka.tools.http.model.AkkaHttpType;
import com.akka.tools.http.model.AkkaRequest;
import com.akka.tools.http.model.AkkaResponse;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.akka.tools.http.model.AkkaHttpHeader.ContentTypeKey;

abstract class AbstractAkkaHttpHandler implements HttpHandler {

    AkkaHttpType[] method;
    String name;

    public abstract void init();
    public abstract void process(AkkaRequest req, AkkaResponse resp);
    public abstract void process0(HttpExchange exchange) throws IOException;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            AkkaHttpType type = AkkaHttpType.valueOf(
                    exchange.getRequestMethod().toUpperCase(Locale.ROOT)
            );
            if (!Arrays.asList(method).contains(type)) {
                throw new IllegalArgumentException(String.format("The handler [%s] supports %s", name, Arrays.toString(method)));
            }

            process0(exchange);
        } catch (Exception e) {
            exchange.sendResponseHeaders(400,0);
            exchange.getResponseBody().write(e.getMessage().getBytes());
        } finally {
            exchange.getResponseBody().close();
        }
    }



}
