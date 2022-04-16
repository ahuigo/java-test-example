package com.ahuigo.tos.demo;

import java.util.HashMap;
import java.util.Map;

import com.ahuigo.tos.sdk.TosHandler;
import com.ahuigo.tos.sdk.TosWorker;

public class TosTest {

    static final int INTERVAL = 5;

    static String taskName = "taskname";

    public static TosHandler userCode() {
        // task中包含了所有任务信息，对task进行处理并输出你的结果，需要按照给定的Result结构返回
        return input -> {
            System.out.println(input);

            Thread.sleep(INTERVAL);
            Map<String, Object> output = new HashMap<>();
            output.put("key1", "val1");
            output.put("key2", 2);
            output.put("key3", 3);
            return output;
        };
    }

    public static void main(String[] args) {
        try {
            TosWorker tosWorker = new TosWorker();
            tosWorker.start(userCode(), taskName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
