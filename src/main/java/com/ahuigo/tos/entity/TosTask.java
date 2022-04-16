package com.ahuigo.tos.entity;

import java.io.Serializable;

public class TosTask implements Serializable {
    private String taskRefName;
    private String workflowName;
    private String hostName;
    private Object inputData;
    private String taskId;
    private String taskName;

    public TosTask() {
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public Object getInputData() {
        return this.inputData;
    }

    public String getTaskDefName() {
        return this.taskRefName;
    }

    public String getWorkflowName() {
        return this.workflowName;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public String getHostName() {
        return this.hostName;
    }

}
