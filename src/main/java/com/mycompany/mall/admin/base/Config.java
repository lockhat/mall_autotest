package com.mycompany.mall.admin.base;

/**
 * @Author: Liu Yue
 * @Date: 2025/3/30 下午11:13
 */

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        try {
            String env = System.getProperty("env", "test");
            String filename = String.format("config-%s.properties", env);
            InputStream is = Config.class.getClassLoader().getResourceAsStream(filename);
            if (is == null) {
                throw new RuntimeException("❌ 配置文件不存在: " + filename);
            }
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException("❌ 加载配置文件失败", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static String getBaseUrl() {
        return get("baseUrl");
    }

    //获取 HTTP 请求超时时间（默认 5000ms）
    public static int getHttpTimeout() {
        return Integer.parseInt(props.getProperty("http.timeout", "5000"));
    }

    //获取最大重试次数（默认 0 次）
    public static int getHttpRetryCount() {
        return Integer.parseInt(props.getProperty("http.retryCount", "0"));
    }

    public static Properties getAll() {
        return props;
    }
}

