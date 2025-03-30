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

    public static Properties getAll() {
        return props;
    }
}

