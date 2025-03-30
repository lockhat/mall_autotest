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
import com.mycompany.mall.admin.framework.TestBase;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

@Listeners(AllureTestNg.class)
public class PreferenceAreaTest extends TestBase {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Description("成功获取优选区商品列表")
    public void testGetListAllSuccess() throws Exception {
        // 1. 获取 token
        String token = getToken("testUser");

        // 2. 构造请求
        String url = "http://60.204.173.174:8080/prefrenceArea/listAll";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", token);

        // 3. 请求接口
        String responseString = HttpClientUtil.doGet(url, headers);
        JsonNode rootNode = objectMapper.readTree(responseString);
        String actualName = rootNode.get("data").get(0).get("name").asText();

        // 4. 查询数据库预期值
        String sql = "SELECT name FROM cms_prefrence_area ORDER BY id ASC LIMIT 1";
        Object expectedNameObj = DBUtil.queryForScalar(sql);
        String expectedName = expectedNameObj != null ? expectedNameObj.toString() : null;

        // 5. 断言
        Assert.assertNotNull(actualName, "接口返回的 name 字段为空");
        Assert.assertEquals(actualName, expectedName, "接口返回的 name 与数据库中不一致");
    }


    @Description("未登录用户无法获取列表")
    @Test
    public void testGetListAllFailure() throws Exception {
        String url = "http://60.204.173.174:8080/prefrenceArea/listAll";

        // 不传 Authorization
//        Map<String, String> headers = Map.of("Content-Type", "application/json");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        // 使用支持状态码返回的工具方法
        HttpResponseWrapper response = HttpClientUtil.doGetWithStatus(url, headers);

        Assert.assertTrue(response.getStatusCode() == 401 || response.getBody().contains("未登录"), "应拒绝未登录访问！");
    }
}
