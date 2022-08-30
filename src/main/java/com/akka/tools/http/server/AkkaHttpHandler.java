package com.akka.tools.http.server;

import com.akka.tools.http.model.AkkaHttpHeader;
import com.akka.tools.http.model.AkkaRequest;
import com.akka.tools.http.model.AkkaResponse;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;

import static com.akka.tools.http.model.AkkaHttpHeader.ContentTypeKey;

@Deprecated
public abstract class AkkaHttpHandler extends AbstractAkkaHttpHandler {


    public String getName() {
        return name;
    }

    @Override
    public void process0(HttpExchange exchange) throws IOException {
        Headers reqHeaders = null;
        Object body = null;

        try {
           reqHeaders = exchange.getRequestHeaders();
           AkkaHttpHeader.ContentType contentType = AkkaHttpHeader.ContentType.get(
                   reqHeaders.get(ContentTypeKey).get(0)
           );
           int contentLength = Integer.parseInt(reqHeaders.get(AkkaHttpHeader.ContentLengthKey).get(0));
           body = body(contentType, exchange.getRequestBody(), contentLength);
           AkkaRequest request = new AkkaRequest(reqHeaders, exchange.getRequestURI(), body);
           AkkaResponse response = new AkkaResponse();

           process(request, response);

           exchange.sendResponseHeaders(200,0);
           exchange.getResponseBody().write(response.getBody().getBytes());

       } catch (Exception e) {
            exchange.sendResponseHeaders(400,0);
            exchange.getResponseBody().write(e.getMessage().getBytes());
        } finally {
            exchange.getResponseBody().close();
        }
    }

    private Object body(AkkaHttpHeader.ContentType contentType, InputStream requestBody, int length) throws IOException {
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
