package com.ahuigo.tos.entity;

import lombok.Data;

@Data
public class HttpResult {
    private int statusCode;
    private String content;

    public boolean success() {
        return statusCode >= 200 && statusCode < 300;
    }
}
