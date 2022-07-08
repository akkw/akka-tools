package com.akka.tools.http.server;

import com.sun.net.httpserver.HttpServer;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class AkkaHttpServerTest {


    @Test
    public void httpServer() throws Exception {
        AkkaHttpServer server = AkkaHttpServer
                .builder()
                .port(9000)
                .contextPath("http/context.xml")
                .executor(Executors.newFixedThreadPool(10))
                .build();
        server.start();
        new CountDownLatch(1).await();
    }
}