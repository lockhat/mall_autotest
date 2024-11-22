package com.mycompany.mall.testbase;

/**
 * @Author: Liu Yue
 * @Date: 2024/11/18 下午2:36
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestBase {
    protected ObjectMapper objectMapper = new ObjectMapper();
    public JsonNode getTestData(String filePath, String key) throws IOException {
        //Path path = Paths.get(filePath);
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JsonNode rootNode = objectMapper.readTree(content);
        return rootNode.get(key);
    }
    public String getToken(String userKey) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        JsonNode testData = getTestData("test-data/login.json", userKey);
        String url = "http://60.204.173.174:8080/admin/login";
        //将 Java 对象转换为 JSON 字符串。
        String json = objectMapper.writeValueAsString(testData);

        HttpPost post = new HttpPost(url);
        post.setHeader("Content-type", "application/json");
        post.setEntity(new StringEntity(json));

        HttpResponse response = httpClient.execute(post);
        String responseString = EntityUtils.toString(response.getEntity());
        ObjectMapper objectMapper = new ObjectMapper();
        //解析json字符串为一个树状结构的对象（即 JsonNode）
        JsonNode rootNode = objectMapper.readTree(responseString);
        //JsonNode（树结构-->节点的方式）提供方法来访问节点的键、值、数组、嵌套结构等
        String token = rootNode.get("data").get("token").asText();
//        String token = dataNode.get("token").asText();

        return token;

    }

    public void logTestResult(ITestResult result) {
        if (result.getStatus() == ITestResult.SUCCESS) {
            System.out.println(result.getName() + " : PASSED");
        } else {
            System.out.println(result.getName() + " : FAILED");
        }
    }
}
