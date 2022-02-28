package com.akka.tools.http.model;

public class AkkaHttpHeader {
    public static final String AcceptEncodingKey = "Accept-Encoding";
    public static final String ContentTypeKey = "Content-Type";
    public static final String ContentLengthKey = "Content-Length";
    public static final String HostKey = "Host";
    public static final String UserAgentKey = "User-Agent";
    public static final String AcceptKey = "Accept";
    public static final String ConnectionKey = "Connection";

    public enum ContentType {
        TEXT("text/plain"),
        JSON("application/json");



        final String name;

        ContentType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static ContentType get(String value) {
            switch (value) {
                case "text/plain":
                    return TEXT;
                case "application/json":
                    return JSON;
            }
            return null;
        }
    }
}
