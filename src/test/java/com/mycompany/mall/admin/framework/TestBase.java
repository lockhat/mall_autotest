package com.mycompany.mall.admin.framework;

/**
 * @Author: Liu Yue
 * @Date: 2024/11/18 下午2:36
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.mall.admin.base.HttpClientUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TestBase {

    protected ObjectMapper objectMapper = new ObjectMapper();

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

        String url = "http://60.204.173.174:8080/admin/login";
        String responseString = HttpClientUtil.doPostJson(url, json);

        JsonNode root = objectMapper.readTree(responseString);
        JsonNode tokenNode = root.path("data").path("token");

        if (tokenNode.isMissingNode() || tokenNode.isNull()) {
            throw new IllegalArgumentException("登录接口响应中未包含 token，请检查响应结构或登录失败");
        }

        return "Bearer " + tokenNode.asText();
    }
}
