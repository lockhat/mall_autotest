package com.mycompany.mall.admin.framework;

import com.mycompany.mall.admin.base.HttpClientUtil;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.ITestListener;

/**
 * @Author: Liu Yue
 * @Date: 2025/4/11 上午10:49
 */
public class TestListener implements ITestListener{
    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        System.out.println("❌ 测试失败：" + methodName);

        // 获取最近一次请求日志
        String log = HttpClientUtil.getLastResponseLog();

        if (log != null) {
            Allure.addAttachment("失败接口请求响应日志", "text/plain", log, "txt");
        } else {
            Allure.addAttachment("失败日志", "text/plain", "未捕获到最近请求记录", "txt");
        }
    }

}
