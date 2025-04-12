package com.mycompany.mall.admin.framework;

/**
 * @Author: Liu Yue
 * @Date: 2024/11/18 下午2:36
 * TestBase 作为所有测试用例的基类，可以集中管理测试过程中需要共享的数据（如 orderId、token 等），避免在每个测试用例中重复定义。
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.mall.admin.base.Config;
import com.mycompany.mall.admin.base.HttpClientUtil;
import org.testng.annotations.BeforeSuite;
import io.qameta.allure.Allure;

import java.io.InputStream;
import java.io.FileWriter;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class TestBase {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected ObjectMapper objectMapper = new ObjectMapper();

    /**
     *
     * @param title
     * @param e
     * 异常捕获并打印到日志和 Allure 报告中
     */
    protected void handleError(String title, Exception e) {
        log.error(title, e);
        // 附件在报告中显示的标题名称；文件类型；文件内容；文件拓展名
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
    public JsonNode getTestData(String resourcePath, String key) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                String msg = "❌ 找不到测试数据文件: " + resourcePath;
                log.error(msg);
                Allure.addAttachment("测试数据文件加载失败", msg);
                throw new IllegalArgumentException(msg);
            }

            JsonNode rootNode = objectMapper.readTree(is);

            // ✅ key 为 null 时返回整个 JSON 节点
            if (key == null) {
                return rootNode;
            }

            JsonNode targetNode = rootNode.get(key);
            if (targetNode == null) {
                String msg = "❌ 找不到 key: " + key + "，请检查 JSON 文件结构";
                log.error(msg);
                Allure.addAttachment("测试数据节点缺失", msg);
                throw new IllegalArgumentException(msg);
            }

            return targetNode;

        } catch (Exception e) {
            handleError("读取测试数据失败", e);
            throw new RuntimeException("读取测试数据失败: " + key, e);
        }
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
