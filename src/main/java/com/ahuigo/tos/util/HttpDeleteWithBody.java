package com.ahuigo.tos.util;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {

    public static final String METHOD_NAME = "DELETE";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpDeleteWithBody(URI uri) {
        super();
        this.setURI(uri);
    }

    public HttpDeleteWithBody(String uri) {
        super();
        this.setURI(URI.create(uri));
    }

    public HttpDeleteWithBody() {
        super();
    }
}
