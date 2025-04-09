package com.mycompany.mall.admin.utils;

/**
 * @Author: Liu Yue
 * @Date: 2025/4/9 下午1:33
 */
public class UrlCoverageRunner {
    public static void main(String[] args) throws Exception {
        System.out.println("🔍 Step 1: 提取测试调用 URL");
        UrlTestedCollector.main(null);

        System.out.println("🌐 Step 2: 对比 Swagger 接口覆盖率");
        UrlCoverageAnalyzer.main(null);
    }
}
