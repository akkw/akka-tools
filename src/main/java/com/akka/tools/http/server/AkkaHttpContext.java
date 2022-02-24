package com.akka.tools.http.server;

class AkkaHttpContext {
    String uri;
    String handlerPath;
    String method;
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getHandlerPath() {
        return handlerPath;
    }

    public void setHandlerPath(String handlerPath) {
        this.handlerPath = handlerPath;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
