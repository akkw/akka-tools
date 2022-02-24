package com.akka.tools.http.model;

import com.sun.net.httpserver.Headers;

import java.net.URI;

public class Request {
    private Headers headers;
    private URI uri;
    private String method;
    private String body;


    public Request() {
    }

    public Request(Headers headers, URI uri, String method, String body) {
        this.headers = headers;
        this.uri = uri;
        this.method = method;
        this.body = body;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
