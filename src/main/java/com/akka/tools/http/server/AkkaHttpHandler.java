package com.akka.tools.http.server;

import com.akka.tools.http.model.AkkaRequest;
import com.akka.tools.http.model.AkkaResponse;

public abstract class AkkaHttpHandler extends AbstractAkkaHttpHandler {


    public String getName() {
        return name;
    }
}
