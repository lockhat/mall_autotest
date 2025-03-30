package com.mycompany.mall.admin.base;

/**
 * @Author: Liu Yue
 * @Date: 2025/3/30 下午4:53
 */

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import java.nio.charset.StandardCharsets;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpClientUtil {

    private static final int TIMEOUT = 5000;

    private static CloseableHttpClient createClient() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT)
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();
    }

    public static String doPostJson(String url, String jsonBody) throws Exception {
        return doPostJson(url, jsonBody, null);
    }

    public static String doPostJson(String url, String jsonBody, Map<String, String> headers) throws Exception {
        try (CloseableHttpClient client = createClient()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");

            if (headers != null) {
                headers.forEach(post::setHeader);
            }

            post.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        }
    }

    public static String doGet(String url, Map<String, String> headers) throws Exception {
        try (CloseableHttpClient client = createClient()) {
            HttpGet get = new HttpGet(url);

            if (headers != null) {
                headers.forEach(get::setHeader);
            }

            try (CloseableHttpResponse response = client.execute(get)) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        }
    }

    /**
     * 发送表单 POST 请求（application/x-www-form-urlencoded）
     */
    public static String doPostForm(String url, Map<String, String> formParams, Map<String, String> headers) throws Exception {
        try (CloseableHttpClient client = createClient()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            if (headers != null) {
                headers.forEach(post::setHeader);
            }

            List<NameValuePair> params = new ArrayList<>();
            for (Map.Entry<String, String> entry : formParams.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }

            post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        }
    }
    public static HttpResponseWrapper doGetWithStatus(String url, Map<String, String> headers) throws Exception {
        try (CloseableHttpClient client = createClient()) {
            HttpGet get = new HttpGet(url);

            if (headers != null) {
                headers.forEach(get::setHeader);
            }

            try (CloseableHttpResponse response = client.execute(get)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                int code = response.getStatusLine().getStatusCode();
                return new HttpResponseWrapper(body, code);
            }
        }
    }

}
