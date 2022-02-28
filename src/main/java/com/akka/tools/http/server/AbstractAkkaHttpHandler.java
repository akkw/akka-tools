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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.akka.tools.http.model.AkkaHttpHeader.ContentTypeKey;

abstract class AbstractAkkaHttpHandler implements HttpHandler {

    AkkaHttpType[] method;
    String name;

    public abstract void init();
    public abstract void process(AkkaRequest req, AkkaResponse resp);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers reqHeaders =  null;
        Object body = null;
        try {
            AkkaHttpType type = AkkaHttpType.valueOf(
                    exchange.getRequestMethod().toUpperCase(Locale.ROOT)
            );

            if (!Arrays.asList(method).contains(type)) {
                throw new IllegalArgumentException(String.format("The handler [%s] supports %s", name, Arrays.toString(method)));
            }
            reqHeaders = exchange.getRequestHeaders();

            ContentType contentType = ContentType.get(
                    reqHeaders.get(ContentTypeKey).get(0)
            );
            int contentLength = Integer.parseInt(reqHeaders.get(AkkaHttpHeader.ContentLengthKey).get(0));
            body = body(contentType, exchange.getRequestBody(), contentLength);
        } catch (Exception e) {

        }
        AkkaRequest request = new AkkaRequest(reqHeaders, exchange.getRequestURI(), body);
        AkkaResponse response = new AkkaResponse();
        process(request, response);
        exchange.sendResponseHeaders(200/**/,0/**/);
    }

    private Object body(ContentType contentType, InputStream requestBody, int length) throws IOException {
        if (length == 0) return "";
        byte[] bytes = new byte[length];
        requestBody.read(bytes);
        String body = new String(bytes);
        switch(contentType) {
            case JSON:
               return JSONObject.parse(body);
            case TEXT:
                return body;
        }
        return null;
    }

}
