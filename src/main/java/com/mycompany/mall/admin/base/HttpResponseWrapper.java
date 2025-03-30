package com.mycompany.mall.admin.base;

/**
 * @Author: Liu Yue
 * @Date: 2025/3/30 下午6:43
 */

public class HttpResponseWrapper {
    private final String body;
    private final int statusCode;

    public HttpResponseWrapper(String body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
