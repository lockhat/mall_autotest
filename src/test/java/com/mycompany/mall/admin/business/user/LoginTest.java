package com.mycompany.mall.admin.business.user;

/**
 * @Author: Liu Yue
 * @Date: 2024/11/18 下午2:38
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.mall.admin.framework.TestBase;
import com.mycompany.mall.admin.base.HttpClientUtil;
import com.mycompany.mall.admin.base.Config;

import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.mycompany.mall.admin.base.Log;
import org.slf4j.Logger;

public class LoginTest extends TestBase {
    private static final Logger log = Log.get(LoginTest.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Description("正常登录")
    @Test
    public void testLoginSuccess() throws Exception {
        log.info("开始执行 testLoginSuccess");
        // 1. 准备测试数据
        JsonNode testData = getTestData("test-data/login.json", "testUser");
        String jsonString = objectMapper.writeValueAsString(testData);
        log.debug("请求体: {}", jsonString);

        // 2. 发起请求
        String url = Config.getBaseUrl() + "/admin/login";
        String responseString = HttpClientUtil.doPostJson(url, jsonString);
        log.debug("响应: {}", responseString);

        // 3. 断言
        Assert.assertTrue(responseString.contains("token"), "返回结果中未包含 token！");
    }

    @Description("用户名不存在")
    @Test
    public void testLoginFailure() throws Exception {
        log.info("开始执行 testLoginFailure");
        // 1. 准备错误测试数据
        JsonNode testData = getTestData("test-data/login.json", "testUserFailure");
        String jsonString = objectMapper.writeValueAsString(testData); //把测试数据（Java 对象）序列化成标准 JSON 字符串，用于发送 POST 请求
        //JsonNode testData = objectMapper.readTree(jsonString); //反过程
        log.debug("请求体: {}", jsonString);

        // 2. 发起请求
        String url = Config.getBaseUrl() + "/admin/login";
        String responseString = HttpClientUtil.doPostJson(url, jsonString);
        log.debug("响应: {}", responseString);
//        System.out.println("Logback config location: " + LoggerFactory.getILoggerFactory().getClass());  //logback写文件排查

        // 3. 断言失败场景
        //Assert.assertEquals(response.getStatusLine().getStatusCode(), 401);
        Assert.assertTrue(responseString.contains("用户名或密码错误"), "未返回预期错误提示！");
    }
}

