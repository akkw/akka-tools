package com.akka.tools.http.server;

import com.akka.tools.api.LifeCycle;
import com.sun.net.httpserver.HttpServer;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

public class AkkaHttpServer implements LifeCycle {

    private final HttpServer server;

    private AkkaHttpServer(HttpServer server) {
        this.server = server;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void stop() {
        server.stop(0);
    }

    static class Builder {
        HttpServer server;
        int port;
        String contextPath;
        Executor executor;
        int tcpMaxConcurrent;

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder contextPath(String contextPath) {
            this.contextPath = contextPath;
            return this;
        }

        public Builder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public Builder tcpMaxConcurrent(int tcpMaxConcurrent) {
            this.tcpMaxConcurrent = tcpMaxConcurrent;
            return this;
        }

        public AkkaHttpServer build() throws IOException, DocumentException,
                ClassNotFoundException, InstantiationException,
                IllegalAccessException, URISyntaxException {
            build0();
            this.server.setExecutor(executor);

            return new AkkaHttpServer(this.server);
        }

        private void build0() throws IOException, DocumentException,
                ClassNotFoundException, InstantiationException,
                IllegalAccessException, URISyntaxException {
            if (this.port < 1024) {
                throw new IllegalArgumentException("port > 1024");
            }
            if (this.contextPath == null || this.contextPath.equals("")) {
                throw new IllegalArgumentException("port > 1024");
            }
            InetSocketAddress port = new InetSocketAddress(this.port);
            this.server = HttpServer.create(port, this.tcpMaxConcurrent);

            context();
        }

        private void context() throws DocumentException, ClassNotFoundException,
                InstantiationException, IllegalAccessException, URISyntaxException {
            List<AkkaHttpContext> contexts = processXML();
            context0(contexts);
        }

        private void context0(List<AkkaHttpContext> contexts) throws ClassNotFoundException,
                InstantiationException, IllegalAccessException {

            for (AkkaHttpContext context : contexts) {
                Class<?> aClass = Class.forName(context.handlerPath);
                AbstractAkkaHttpHandler o = (AbstractAkkaHttpHandler) aClass.newInstance();
                o.method = context.method.split(",");
                o.init();
                this.server.createContext(context.uri, o);
            }
        }

        private List<AkkaHttpContext> processXML() throws DocumentException, URISyntaxException {

            URL fileURL = ClassLoader.getSystemResource(this.contextPath);
            File file = new File(fileURL.toURI());

            SAXReader reader = new SAXReader();
            Document read = reader.read(file);
            Element akkaHttp = read.getRootElement();
            Iterator<Element> cxtsIterator = akkaHttp.elementIterator("contexts");

            List<AkkaHttpContext> contexts = new ArrayList<>();
            AkkaHttpContext context;

            while (cxtsIterator.hasNext()) {
                Element ctx = cxtsIterator.next();
                final Iterator<Element> ctxItr = ctx.elementIterator();

                while (ctxItr.hasNext()) {
                    context = new AkkaHttpContext();

                    Element next = ctxItr.next();
                    List<Attribute> attributes = next.attributes();
                    Iterator<Element> ctxElemItr = next.elementIterator();
                    for (Attribute a : attributes) {
                        if (a.getName().equals("uri")) {
                            context.uri = a.getValue();
                        }
                        if (a.getName().equals("handel")) {
                            context.handlerPath = a.getValue();
                        }
                    }

                    while(ctxElemItr.hasNext()) {
                        Element element = ctxElemItr.next();
                        String name = element.getName();
                        if (name.equals("method")) {
                            context.method = element.getText();
                        }


                    }
                    contexts.add(context);
                }

            }
            return contexts;
        }
    }
}
