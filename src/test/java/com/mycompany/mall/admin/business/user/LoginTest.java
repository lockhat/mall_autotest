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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginTest extends TestBase {
    @Test(dataProvider = "loginData")
    @Description("登录接口测试（JSON 数据驱动）")
    public void testLoginFromJson(String username, String password, String expected, String desc) {
        log.info("开始执行用例：{}", desc);
        try {
            // 构造请求体
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> map = new HashMap<>();
            map.put("username", username);
            map.put("password", password);
            String jsonBody = objectMapper.writeValueAsString(map);

            String url = "/admin/login";
            String responseString = HttpClientUtil.doPostJson(Config.getBaseUrl() + url, jsonBody);
            log.debug("请求地址: [POST] " + url);
            log.debug("请求体: {}", jsonBody);
            log.debug("响应: {}", responseString);

            Assert.assertTrue(responseString.contains(expected), "响应未包含预期值：" + expected);
        } catch (Exception e) {
            handleError(desc + " - 异常", e);
            Assert.fail("测试用例执行失败：" + e.getMessage());
        }
    }

    @DataProvider(name = "loginData")
    public Object[][] loginData() throws Exception {
        JsonNode root = getTestData("test-data/login.json", null);
        List<Object[]> dataList = new ArrayList<>();

        root.fields().forEachRemaining(entry -> {
            JsonNode node = entry.getValue();
            dataList.add(new Object[]{
                    node.get("username").asText(),
                    node.get("password").asText(),
                    node.get("expected").asText(),
                    node.get("desc").asText()
            });
        });

        return dataList.toArray(new Object[0][]);
    }

}

