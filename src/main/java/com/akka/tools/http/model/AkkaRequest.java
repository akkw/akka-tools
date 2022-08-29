package com.akka.tools.http.model;

import com.sun.net.httpserver.Headers;

import java.net.URI;
@Deprecated
public class AkkaRequest {
    private Headers headers;
    private URI uri;
    private Object body;


    public AkkaRequest() {
    }

    public AkkaRequest(Headers headers, URI uri, Object body) {
        this.headers = headers;
        this.uri = uri;
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

    public Object getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
