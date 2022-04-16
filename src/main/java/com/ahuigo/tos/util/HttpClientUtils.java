package com.ahuigo.tos.util;

import com.ahuigo.tos.entity.HttpResult;
import com.ahuigo.tos.exception.HttpStatusException;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class HttpClientUtils {
    public HttpClientUtils() {
    }

    public static HttpResult doGet(String url) throws IOException {
        log.info("Begin to get {}", url);

        HttpResult result = new HttpResult();

        try (CloseableHttpClient httpclient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setRetryHandler(new DefaultHttpRequestRetryHandler())
                .build()) {
            HttpGet httpGet = new HttpGet(url);

            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result.setContent(EntityUtils.toString(entity));
                }
                result.setStatusCode(statusCode);
            }
        }

        return result;
    }

    public static String doPost(String url, String body) throws IOException, HttpStatusException {
        return doPost(url, body, null);
    }

    public static String doPost(String url, String body, Map<String, String> headers)
            throws IOException, HttpStatusException {
        log.info("Begin to post {}", url);
        String result = "";

        try (CloseableHttpClient httpclient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setRetryHandler(new DefaultHttpRequestRetryHandler())
                .build()) {

            HttpPost httpPost = new HttpPost(url);

            httpPost.addHeader("Content-Type", "application/json");
            if (headers != null) {
                for (String key : headers.keySet()) {
                    httpPost.addHeader(key, headers.get(key));
                }
            }

            StringEntity entity = new StringEntity(body, "utf-8");
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    result = EntityUtils.toString(responseEntity);
                }

                if (statusCode < 200 || statusCode >= 300) {
                    throw new HttpStatusException(String.format("HttpClientUtils.doPost：%s %s", url, result),
                            String.valueOf(statusCode));
                }
            }
        }

        return result;
    }

    public static String doPut(String url, String body) throws IOException, HttpStatusException {
        return doPut(url, body, null);
    }

    public static String doPut(String url, String body, Map<String, String> headers)
            throws IOException, HttpStatusException {
        log.info("Begin to put {}", url);
        String result = "";

        try (CloseableHttpClient httpclient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setRetryHandler(new DefaultHttpRequestRetryHandler())
                .build()) {

            HttpPut httpPut = new HttpPut(url);
            StringEntity entity = new StringEntity(body, "utf-8");
            httpPut.setEntity(entity);

            httpPut.addHeader("Content-Type", "application/json");

            if (headers != null) {
                for (String key : headers.keySet()) {
                    httpPut.addHeader(key, headers.get(key));
                }
            }

            try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    result = EntityUtils.toString(responseEntity);
                }

                if (statusCode < 200 || statusCode >= 300) {
                    throw new HttpStatusException(String.format("HttpClientUtils.doPut：%s %s", url, result),
                            String.valueOf(statusCode));
                }
            }
        }

        return result;
    }

    public static String doDelete(String url) throws IOException, HttpStatusException {
        return doDelete(url, null);
    }

    public static String doDelete(String url, String body) throws IOException, HttpStatusException {
        return doDelete(url, body, null);
    }

    public static String doDelete(String url, String body, Map<String, String> headers)
            throws IOException, HttpStatusException {
        log.info("Begin to delete {}", url);

        String result = "";

        try (CloseableHttpClient httpclient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setRetryHandler(new DefaultHttpRequestRetryHandler())
                .build()) {
            HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);

            httpDelete.addHeader("Content-type", "application/json");

            if (!Strings.isNullOrEmpty(body)) {
                log.info("Delete body: {}", body);
                StringEntity entity = new StringEntity(body, "utf-8");
                httpDelete.setEntity(entity);
            }

            if (headers != null) {
                for (String key : headers.keySet()) {
                    httpDelete.addHeader(key, headers.get(key));
                }
            }

            try (CloseableHttpResponse response = httpclient.execute(httpDelete)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    result = EntityUtils.toString(responseEntity);
                }

                if (statusCode < 200 || statusCode >= 300) {
                    log.error("Error code: {}", statusCode);
                    throw new HttpStatusException(String.format("HttpClientUtils.doDelete：%s %s", url, result),
                            String.valueOf(statusCode));
                }
            }
        }

        return result;
    }
}
