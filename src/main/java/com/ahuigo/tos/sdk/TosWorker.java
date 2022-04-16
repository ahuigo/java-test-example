package com.ahuigo.tos.sdk;

import com.ahuigo.tos.entity.HttpResult;
import com.ahuigo.tos.entity.TosResult;
import com.ahuigo.tos.entity.TosTask;
import com.ahuigo.tos.exception.HttpStatusException;
import com.ahuigo.tos.util.HttpClientUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TosWorker {
    private final String tosUrl;
    private final TosTask tosTask;
    private final TosHelper helper;

    private static final String[] PRIORITY_LIST = new String[] { "high", "" };
    private String taskName;
    private final int interval;
    private String hostName;
    private final String domainName;

    public TosWorker() throws Exception {
        this("domain");
    }

    public TosWorker(String domainName) throws Exception {
        this.interval = 1;
        this.tosUrl = "http://x.com/";
        this.domainName = domainName;
        this.tosTask = new TosTask();

        helper = new TosHelper("dev", domainName);
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void executeOneTask(TosHandler handler, TosTask input) {
        if (input != null) {
            HashMap<String, Object> taskCheck = new HashMap<>();
            taskCheck.put("task_uuid", input.getTaskId());
            taskCheck.put("task_name", input.getTaskDefName());
            taskCheck.put("workflow_name", input.getWorkflowName());
            taskCheck.put("pod_name", this.hostName);
            taskCheck.put("input", input.getInputData());
            String checkId = this.register(taskCheck);
            ScheduledExecutorService heartBeatExecutor = this.heartBeat(checkId);
            TosResult tosResult = new TosResult();

            try {
                Object workerInput = input.getInputData();
                log.info("Begin to execute {}", input.getTaskId());
                Object output = handler.apply(workerInput);
                tosResult.setOutput(output);
                tosResult.task_id = input.getTaskId();
                tosResult.domain = this.domainName;
                tosResult.error = "";
                tosResult.error_type = "";

                log.info("Finished to execute {}", input.getTaskId());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                tosResult.setError(e.toString());
                tosResult.setError_type("WorkerFail");
            } finally {
                tosResult.setTask_id(input.getTaskId());

                this.destroy(checkId, heartBeatExecutor, tosResult);
            }
        }
    }

    public String register(HashMap<String, Object> taskCheck) {
        String checkId = null;
        String registerUrl = String.format("%s/register", "http://x.com");

        try {
            String body = JSON.toJSONString(taskCheck);
            checkId = HttpClientUtils.doPost(registerUrl, body);
            log.info("register {} successful", checkId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return checkId;
    }

    public ScheduledExecutorService heartBeat(final String checkId) {
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(
                1,
                (new BasicThreadFactory.Builder()).namingPattern("schedule-pool-%d").daemon(false).build());
        executorService.scheduleAtFixedRate(() -> {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
            log.info("Send heartbeat, checkID:{}, at time P{}\n", checkId, dateFormat.format(date));

            String heartBeatUrl = String.format("%s/api/v1/task/checks/%s", this.tosUrl, checkId);
            try {
                HttpClientUtils.doPut(heartBeatUrl, "{\"status\": \"healthy\"}");
            } catch (IOException | HttpStatusException e) {
                log.error(e.getMessage(), e);
            }
        }, 5L, 5L, TimeUnit.SECONDS);
        return executorService;
    }

    public void destroy(String checkId, ScheduledExecutorService heartBeatExecutor, TosResult tosResult) {
        heartBeatExecutor.shutdown();
        String deleteWorkerUrl = String.format("%s/api/v1/task/checks/%s", this.tosUrl, checkId);

        try {
            HttpClientUtils.doDelete(deleteWorkerUrl, tosResult.toJson(), helper.getHeaders());
            log.info("deregister {} successful", checkId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public String getFullTaskName(String name, String priority) {
        return "high".equals(priority) ? String.format("%s@%s", name, priority) : name;
    }

    public TosTask getTask(String name) {
        for (String priority : PRIORITY_LIST) {
            String taskType = this.getFullTaskName(name, priority);

            try {
                Thread.sleep(this.interval * 1000L);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            String getTaskUrl = String.format("%s/api/v1/queues/poll?", this.tosUrl);
            if (!Strings.isNullOrEmpty(taskType)) {
                getTaskUrl = getTaskUrl + String.format("name=%s", taskType);
            }

            if (this.hostName != null && !Strings.isNullOrEmpty(this.hostName)) {
                getTaskUrl = getTaskUrl + String.format("&poller_id=%s", this.hostName);
            }

            if (this.domainName != null && !Strings.isNullOrEmpty(this.domainName)) {
                getTaskUrl = getTaskUrl + String.format("&domain=%s", this.domainName);
            }

            try {
                HttpResult resp = HttpClientUtils.doGet(getTaskUrl);
                if (resp.success() && !Strings.isNullOrEmpty(resp.getContent())) {
                    TosTask task = JSON.parseObject(resp.getContent(), TosTask.class);
                    if (task != null) {
                        return task;
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return null;
    }

    public void start(TosHandler activityHandler, String taskName) {
        this.taskName = taskName;

        try {
            this.hostName = InetAddress.getLocalHost().getHostName();
            this.tosTask.setHostName(this.hostName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            while (true) {
                try {
                    this.executeOneTask(activityHandler, this.getTask(this.getTaskName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
