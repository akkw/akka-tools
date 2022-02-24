package com.akka.tools.http.server;

import com.akka.tools.http.model.Request;
import com.akka.tools.http.model.Response;

public abstract class AkkaHttpHandler extends AbstractAkkaHttpHandler {
    public abstract void process(Request req, Response resp);
}
