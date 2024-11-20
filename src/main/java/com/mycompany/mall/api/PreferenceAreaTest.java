package com.mycompany.mall.api;

/**
 * @Author: Liu Yue
 * @Date: 2024/11/18 下午2:38
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.mall.testbase.TestBase;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.testng.AllureTestNg;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(AllureTestNg.class)
public class PreferenceAreaTest extends TestBase {

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
    @Description("成功获取优选区商品列表")
    @Step("输入：登录用户有查看列表的权限 预期：返回状态码 200，返回的商品列表不为空")
    @Test
    public void testGetListAllSuccess() throws Exception {
        String token = "Bearer " + getToken("testUser");
        String url = "http://60.204.173.174:8080/prefrenceArea/listAll";

        HttpGet get = new HttpGet(url);
        get.setHeader("Content-type", "application/json");
        get.setHeader("Authorization", token);

        HttpResponse response = httpClient.execute(get);
        String responseString = EntityUtils.toString(response.getEntity());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseString);
        String name1 = rootNode.get("data").get(0).get("name").asText();
//        System.out.print(name1);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        Assert.assertEquals(name1, "让音质更出众");
    }

    @Test
    public void testGetListAllFailure() throws Exception {


    }
}
