package com.mycompany.mall.admin.base;

/**
 * @Author: Liu Yue
 * @Date: 2025/3/30 下午4:53
 */

import io.qameta.allure.Allure;
import org.apache.http.NameValuePair;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.SSLException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import java.util.*;

public class HttpClientUtil {
    // 新增一个全局响应缓存
    private static final ThreadLocal<String> lastResponseLog = new ThreadLocal<>();
    // 最后一次请求/响应日志
    public static String getLastResponseLog() {
        return lastResponseLog.get();
    }

    //创建HttpClient实例（配置超时和重试）
    private static CloseableHttpClient createClient() {

        int timeout = Config.getHttpTimeout();
        int maxRetry = Config.getHttpRetryCount();

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout) //连接超时
                .setSocketTimeout(timeout)  //传输超时
                .build();

        HttpRequestRetryHandler retryHandler = (exception, executionCount, context) -> {
            if (executionCount > maxRetry) return false;

            if (exception instanceof InterruptedIOException
                    || exception instanceof UnknownHostException
                    || exception instanceof ConnectTimeoutException
                    || exception instanceof SSLException) {
                return false;
            }

            HttpRequest request = HttpClientContext.adapt(context).getRequest();
            // 只对 GET、HEAD 等幂等请求重试
            return !(request instanceof HttpEntityEnclosingRequest);
        };


        //创建HttpClient实例
        return HttpClients.custom()
                .setDefaultRequestConfig(config)
                .setRetryHandler(retryHandler)
                .build();
    }

    /**
     * 处理post请求--无header
     * @param url
     * @param jsonBody
     * @return
     * @throws Exception
     */
    public static String doPostJson(String url, String jsonBody) throws Exception {
        return doPostJson(url, jsonBody, null);
    }

    /**
     * 处理post请求
     * @param url
     * @param jsonBody
     * @param headers
     * @return
     * @throws Exception
     */
    public static String doPostJson(String url, String jsonBody, Map<String, String> headers) throws Exception {
        try (CloseableHttpClient client = createClient()) {
            //实例化一个HTTP POST请求对象
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json"); // 必需头

            /** 添加自定义头
             * 1、遍历 headers 中的每个键值对（key, value）
             * 2、对每个键值对调用 post.setHeader(key, value)
             */
            if (headers != null) {
                headers.forEach(post::setHeader);
            }

            // 设置请求体（JSON 格式）
            post.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));//StringEntity：将JSON字符串封装为HTTP实体

            // 执行请求并解析响应
            // 📎 Allure 步骤 & 🔄 日志记录
            return logAndExecute("POST 请求: " + url, "【POST 请求】\n地址: " + url + "\n请求体: " + jsonBody, () -> {
                try (CloseableHttpResponse response = client.execute(post)) {
                    return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                }
            });
        }
    }

    /**
     * 处理get请求
     * @param url
     * @param headers
     * @return
     * @throws Exception
     */
    public static String doGet(String url, Map<String, String> headers) throws Exception {
        try (CloseableHttpClient client = createClient()) {
            HttpGet get = new HttpGet(url);

            if (headers != null) {
                headers.forEach(get::setHeader);
            }

            return logAndExecute("GET 请求: " + url, "【GET 请求】\n地址: " + url, () -> {
                try (CloseableHttpResponse response = client.execute(get)) {
                    return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                }
            });
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

            return logAndExecute("POST 表单请求: " + url, "【POST 表单请求】\n地址: "+ url + "\n请求体: ", () -> {
                try (CloseableHttpResponse response = client.execute(post)) {
                    return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                }
            });

        }
    }
    public static HttpResponseWrapper doGetWithStatus(String url, Map<String, String> headers) throws Exception {
        try (CloseableHttpClient client = createClient()) {
            HttpGet get = new HttpGet(url);

            if (headers != null) {
                headers.forEach(get::setHeader);
            }

            return logAndExecute("GET 请求（含状态）: " + url, "【GET 含状态】\n地址: " + url, () -> {
                try (CloseableHttpResponse response = client.execute(get)) {
                    String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    int code = response.getStatusLine().getStatusCode();
                    return new HttpResponseWrapper(body, code);
                }
            });
        }
    }
    private static <T> T logAndExecute(String stepName, String logTitle, ThrowingSupplier<T> action) {
        return Allure.step(stepName, () -> {
            try {
                T result = action.get(); // 执行真实逻辑
                String log = logTitle + "\n" + result;

                lastResponseLog.set(log);
                System.out.println(log);

                Allure.addAttachment("请求日志", new ByteArrayInputStream(log.getBytes(StandardCharsets.UTF_8)));
                return result;
            } catch (Exception e) {
                String errorLog = logTitle + "\n[异常]: " + e.getMessage();
                lastResponseLog.set(errorLog);
                Allure.addAttachment("请求异常日志", errorLog);
                throw e;
            }
        });
    }


}
