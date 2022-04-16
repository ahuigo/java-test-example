package com.ahuigo.tos.sdk;

@FunctionalInterface
public interface TosHandler {
    Object apply(Object input) throws Exception;
}
