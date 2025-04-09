package com.mycompany.mall.admin.utils;

/**
 * @Author: Liu Yue
 * @Date: 2025/4/9 下午3:03
 */

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatusCodeCoverageRunner {

    public static void main(String[] args) throws Exception {
        System.out.println("📦 Step 1: 读取 Swagger 接口状态码定义...");
        StatusCodeSwaggerExtractor.main(null);

        System.out.println("\n📂 Step 2: 提取测试中产生的状态码...");
        String todayLogFile = getTodayLogPath("target/logs");

        if (todayLogFile == null) {
            System.err.println("❌ 未找到今天的日志文件！");
            return;
        }

        System.out.println("📄 选中日志文件：" + todayLogFile);
        StatusCodeTestedCollector.LOG_FILE = todayLogFile;
        StatusCodeTestedCollector.main(null);

        System.out.println("\n📊 Step 3: 状态码维度覆盖率统计分析...");
        StatusCodeCoverageAnalyzer.main(null);
    }

    // 根据当前日期拼接日志路径，例如：target/logs/test-2025-04-09.log
    private static String getTodayLogPath(String baseDir) {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String logPath = baseDir + "/test-" + today + ".log";

        File file = new File(logPath);
        return file.exists() ? file.getPath() : null;
    }
}

