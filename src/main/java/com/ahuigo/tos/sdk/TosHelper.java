package com.ahuigo.tos.sdk;

import com.ahuigo.tos.constant.Constant;
import com.ahuigo.tos.exception.HttpStatusException;
import com.ahuigo.tos.util.HttpClientUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Data // env 对应getEnv
@Slf4j // log.error(e.getMessage(), e); 注解会引用log
public class TosHelper {
    public TosHelper(String env, String domainName) {
        this.domainName = domainName;
    }

    private String env;
    private String domainName;
    private String token;
    private Map<String, String> headers = new HashMap<>(Constant.DEFAULT_SIZE);

    private String getPrefix() {
        if (Constant.DEV.equals(getEnv())) {
            return "dev-";
        } else {
            return "";
        }
    }

    private String getLoginUrl() {
        return String.format("http://%s.x.com/login?", getPrefix());
    }

    public String updateToken() {
        Map<String, Object> params = new HashMap<String, Object>(Constant.DEFAULT_SMALL_SIZE) {
            {
                put("name", "ahuigo");
                put("password", "password");
            }
        };

        try {
            String url = getLoginUrl();
            String res = HttpClientUtils.doPost(url, JSON.toJSONString(params));
            JSONObject obj = JSON.parseObject(res);
            token = obj.getString("id_token");
            log.info("Fetch new token :{}", token);
            headers = new HashMap<String, String>(Constant.DEFAULT_SMALL_SIZE) {
                {
                    put("Cookie", "id_token=" + token);
                }
            };
        } catch (IOException | HttpStatusException e) {
            log.info(e.getMessage(), e);
            return null;
        }

        return token;
    }

    public String getToken() {
        if (Strings.isNullOrEmpty(token)) {
            token = updateToken();
        }

        return token;
    }

    public Map<String, String> getHeaders() {
        if (Strings.isNullOrEmpty(token)) {
            token = updateToken();
        }

        return headers;
    }
}
