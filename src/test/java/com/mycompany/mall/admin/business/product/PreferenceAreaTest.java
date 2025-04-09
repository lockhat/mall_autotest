package com.mycompany.mall.admin.business.product;

/**
 * @Author: Liu Yue
 * @Date: 2024/11/18 下午2:38
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.mall.admin.base.DBUtil;
import com.mycompany.mall.admin.base.HttpClientUtil;
import com.mycompany.mall.admin.base.HttpResponseWrapper;
import com.mycompany.mall.admin.base.Config;

import com.mycompany.mall.admin.framework.TestBase;

import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class PreferenceAreaTest extends TestBase {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Description("成功获取优选区商品列表")
    public void testGetListAllSuccess() {
        log.info("开始执行 testGetListAllSuccess");
        try {
            // 1. 获取 token
            String token = getToken("testUser");

            // 2. 构造请求
            String url = "/prefrenceArea/listAll";
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", token);

            // 3. 请求接口
            String responseString = HttpClientUtil.doGet(Config.getBaseUrl() + url, headers);
            log.debug("请求地址: [GET] " + url);
            log.debug("响应: {}", responseString);

            JsonNode rootNode = objectMapper.readTree(responseString);
            String actualName = rootNode.get("data").get(0).get("name").asText();
            log.info("接口返回 name: {}", actualName);

            // 4. 查询数据库预期值
            String sql = "SELECT name FROM cms_prefrence_area ORDER BY id ASC LIMIT 1";
            Object expectedNameObj = DBUtil.queryForScalar(sql);
            String expectedName = expectedNameObj != null ? expectedNameObj.toString() : null;
            log.info("数据库返回 name: {}", expectedName);

            // 5. 断言
            Assert.assertNotNull(actualName, "接口返回的 name 字段为空");
            Assert.assertEquals(actualName, expectedName, "接口返回的 name 与数据库中不一致");

        } catch (Exception e) {
            handleError("【正常获取优选区商品列表】用例执行失败，抛出异常", e);
            Assert.fail("testGetListAllSuccess 执行失败：" + e.getMessage());
        }
    }


    @Description("未登录用户无法获取列表")
    @Test
    public void testGetListAllFailure() {
        log.info("开始执行 testGetListAllFailure");
        try {
            String url = "/prefrenceArea/listAll";

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            HttpResponseWrapper response = HttpClientUtil.doGetWithStatus(Config.getBaseUrl() + url, headers);
            log.debug("请求地址: [GET] " + url);
            log.debug("响应: {}", response.getBody());

            Assert.assertTrue(response.getStatusCode() == 401 || response.getBody().contains("未登录"),
                    "应拒绝未登录访问！");
        } catch (Exception e) {
            handleError("【未登录用户无法获取列表】用例执行失败，抛出异常", e);
            Assert.fail("testGetListAllFailure 执行失败：" + e.getMessage());
        }
    }
}
