package com.ahuigo.tos.exception;

public class HttpStatusException extends Exception {
    private static final long serialVersionUID = 1L;
    private String msg;
    private String code = "500";

    public HttpStatusException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public HttpStatusException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public HttpStatusException(String msg, String code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public HttpStatusException(String msg, String code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getCode() {
        return this.code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
