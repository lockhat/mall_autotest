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

public class LoginTest extends TestBase {
//    private static final Logger log = Log.get(LoginTest.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Description("正常登录，获取token")
    @Test
    public void testLoginSuccess() {
        log.info("开始执行 testLoginSuccess");
        try {
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
        } catch (Exception e) {
            handleError("登录成功用例异常", e);  // ✅ 异常记录到日志 & Allure 附件
            Assert.fail("执行 testLoginSuccess 失败：" + e.getMessage());
        }
    }

    @Description("用户名不存在")
    @Test
    public void testLoginFailure() {
        log.info("开始执行 testLoginFailure");
        try {
            // 1. 准备错误测试数据
            JsonNode testData = getTestData("test-data/login.json", "testUserFailure");
            String jsonString = objectMapper.writeValueAsString(testData);
            log.debug("请求体: {}", jsonString);

            // 2. 发起请求
            String url = Config.getBaseUrl() + "/admin/login";
            String responseString = HttpClientUtil.doPostJson(url, jsonString);
            log.debug("响应: {}", responseString);

            // 3. 断言失败场景
            Assert.assertTrue(responseString.contains("用户名或密码错误"), "未返回预期错误提示！");
        } catch (Exception e) {
            handleError("登录失败用例异常", e);
            Assert.fail("执行 testLoginFailure 失败：" + e.getMessage());
        }
    }
}

