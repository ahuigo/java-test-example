package com.ahuigo.tos.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

public class TosResult implements Serializable {
    public Object output;
    public String error;
    public String error_type;
    public String task_id;
    public String domain;

    public TosResult() {
    }

    public Object getOutput() {
        return this.output;
    }

    public String getError() {
        return this.error;
    }

    public String getError_type() {
        return this.error_type;
    }

    public String getTask_id() {
        return this.task_id;
    }

    public void setOutput(Object output) {
        this.output = output;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setError_type(String error_type) {
        this.error_type = error_type;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public boolean canEqual(Object other) {
        return other instanceof TosResult;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    @Override
    public String toString() {
        return "Result(output=" + this.getOutput() + ", error=" + this.getError() + ", error_type="
                + this.getError_type() + ", task_id=" + this.getTask_id() + ")";
    }
}
