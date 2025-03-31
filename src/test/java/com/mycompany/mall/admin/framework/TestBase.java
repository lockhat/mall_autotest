package com.mycompany.mall.admin.framework;

/**
 * @Author: Liu Yue
 * @Date: 2024/11/18 下午2:36
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.mall.admin.base.Config;
import com.mycompany.mall.admin.base.HttpClientUtil;
import com.mycompany.mall.admin.base.Log;
import org.testng.annotations.BeforeSuite;
import io.qameta.allure.Allure;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileWriter;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;

public class TestBase {

    protected final Logger log = Log.get(this.getClass());
    protected ObjectMapper objectMapper = new ObjectMapper();

    /**
     *
     * @param title
     * @param e
     * 异常捕获并打印到日志和 Allure 报告中
     */
    protected void handleError(String title, Exception e) {
        log.error(title, e);
        Allure.addAttachment(title + "-StackTrace", "text/plain", e.toString(), "txt");
    }


    @BeforeSuite
    public void generateAllureEnv() throws Exception {
        Properties props = Config.getAll(); // 已从 config-*.properties 读取过

        // 只挑选你希望展示在 Allure 报告中的字段
        Properties allureProps = new Properties();
        allureProps.setProperty("env", props.getProperty("env", "test"));
        allureProps.setProperty("baseUrl", props.getProperty("baseUrl"));

        File file = new File("target/allure-results/environment.properties");
        file.getParentFile().mkdirs(); // 确保目录存在
        try (FileWriter writer = new FileWriter(file)) {
            allureProps.store(writer, "Allure 环境信息");
        }
    }

    /**
     * 从 classpath 中读取 JSON 文件，并返回指定 key 对应的数据节点
     */
    public JsonNode getTestData(String resourcePath, String key) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalArgumentException("找不到测试数据文件: " + resourcePath);
        }

        JsonNode rootNode = objectMapper.readTree(is);
        JsonNode targetNode = rootNode.get(key);
        if (targetNode == null) {
            throw new IllegalArgumentException("找不到 key: " + key + "，请检查 JSON 文件结构");
        }

        return targetNode;
    }

    /**
     * 从 login.json 中读取用户信息，调用登录接口获取 token
     */
    public String getToken(String userKey) throws Exception {
        JsonNode testData = getTestData("test-data/login.json", userKey);
        String username = testData.get("username").asText();
        String password = testData.get("password").asText();

        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);

        String json = objectMapper.writeValueAsString(body);
        String url = Config.getBaseUrl() + "/admin/login";

        String responseString = HttpClientUtil.doPostJson(url, json);

        JsonNode root = objectMapper.readTree(responseString);
        JsonNode tokenNode = root.path("data").path("token");

        if (tokenNode.isMissingNode() || tokenNode.isNull()) {
            throw new IllegalArgumentException("登录接口响应中未包含 token，请检查响应结构或登录失败");
        }

        return "Bearer " + tokenNode.asText();
    }
}
