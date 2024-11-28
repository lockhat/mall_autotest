package com.mycompany.mall.api;

/**
 * @Author: Liu Yue
 * @Date: 2024/11/18 下午2:38
 */
import com.mycompany.mall.testbase.TestBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.testng.AllureTestNg;

import io.qameta.allure.Story;

import java.io.IOException;

@Listeners(AllureTestNg.class)
public class LoginTest extends TestBase {

    private HttpClient httpClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeMethod
    public void setup() {
        // 初始化HTTP客户端
        httpClient = HttpClients.createDefault();
    }

    @AfterMethod
    public void teardown() {
        // 释放资源，例如关闭HTTP客户端连接
        httpClient = null; // 在实际应用中，这里可能需要更复杂的资源释放逻辑

    }
    @Description("正常登录")
    @Step("输入：有效的用户名和密码 预期：登录成功，返回状态码 200，返回包含 token")
    @Test
    public void testLoginSuccess() throws Exception {
        JsonNode testData = getTestData("test-data/login.json", "testUser");
        //将 Java 对象(JsonNode)转换为 JSON 字符串。
        String json = objectMapper.writeValueAsString(testData);

        String url = "http://60.204.173.174:8080/admin/login";

        HttpPost post = new HttpPost(url);
        post.setHeader("Content-type", "application/json");
        /**
        *在 Apache HttpClient 中，StringEntity 是一个类，
        *用于将字符串数据封装为 HTTP 请求的实体（HttpEntity）。
        *它常用于 POST 或 PUT 请求的场景中，作为请求的主体内容。
         */
        post.setEntity(new StringEntity(json));

        HttpResponse response = httpClient.execute(post);
        String responseString = EntityUtils.toString(response.getEntity());

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        Assert.assertTrue(responseString.contains("token"));
    }
    @Description("用户名不存在")
    @Step("输入：无效的用户名 预期：登录失败，返回状态码 401")
    @Test
    public void testLoginFailure() throws Exception {
        System.out.println("Starting test execution testLoginFailure");
        JsonNode testData = getTestData("test-data/login.json", "testUserFailure");
        //String url = "http://60.204.173.174:8080/admin/login";

        String url = "http://localhost:8080/admin/login";

        String json = objectMapper.writeValueAsString(testData);

        HttpPost post = new HttpPost(url);
        post.setHeader("Content-type", "application/json");
        post.setEntity(new StringEntity(json));

        HttpResponse response = httpClient.execute(post);
        String responseString = EntityUtils.toString(response.getEntity());

        //Assert.assertEquals(response.getStatusLine().getStatusCode(), 401);
        //Assert.assertTrue(responseString.contains("error"));
        Assert.assertTrue(responseString.contains("用户名或密码错误"));

    }
}
