package com.mycompany.mall.admin.utils;

/**
 * @Author: Liu Yue
 * @Date: 2025/4/9 下午2:08
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class StatusCodeCoverageAnalyzer {

    private static final String SWAGGER_CODES_FILE = "target/coverage/swagger_status_codes.json";
    private static final String TESTED_CODES_FILE = "target/coverage/tested_status_codes.json";

    public static void main(String[] args) throws Exception {
        List<String> reportLines = new ArrayList<>();
        reportLines.add("# 接口状态码覆盖率报告");
        reportLines.add("");

        Map<String, Set<String>> swaggerMap = readJsonMap(SWAGGER_CODES_FILE);
        Map<String, Set<String>> testedMap = readJsonMap(TESTED_CODES_FILE);


        int totalDefined = 0;
        int totalCovered = 0;

        for (String api : swaggerMap.keySet()) {
            Set<String> defined = swaggerMap.getOrDefault(api, Collections.emptySet());
            Set<String> tested = testedMap.getOrDefault(api, Collections.emptySet());

            Set<String> covered = new HashSet<>(defined);
            covered.retainAll(tested);

            Set<String> uncovered = new HashSet<>(defined);
            uncovered.removeAll(tested);

            totalDefined += defined.size();
            totalCovered += covered.size();

            double rate = defined.isEmpty() ? 1.0 : (double) covered.size() / defined.size();

            reportLines.add("### " + api);
            reportLines.add("- Swagger 定义状态码: " + defined);
            reportLines.add("- 测试触发状态码: " + tested);
            reportLines.add("- 未覆盖状态码: " + uncovered);
            reportLines.add(String.format("- 覆盖率: %.2f%%", rate * 100));
            reportLines.add("");
        }


        double totalRate = (totalDefined == 0) ? 1.0 : (double) totalCovered / totalDefined;
        String summary = String.format("✅ 总状态码覆盖率：%.2f%%（%d/%d）",
                totalRate * 100, totalCovered, totalDefined);
        System.out.println(summary);
        reportLines.add("---");
        reportLines.add(summary);
        // ✅ 写入 markdown 文件
        writeMarkdownReport(reportLines);

        // ✅ 写入 allure 环境变量
        writeAllureEnvironment(totalRate, totalCovered, totalDefined);

        // 🔔 阈值判断（默认 80%）
        double threshold = 0.8;
        if (totalRate < threshold) {
            System.err.printf("❌ 状态码覆盖率低于 %.2f%%，构建失败！\n", threshold * 100);
            System.exit(1); // Jenkins 会识别非0为失败
        } else {
            System.out.println("✅ 状态码覆盖率符合预期 ✅");
        }
    }

    private static Map<String, Set<String>> readJsonMap(String filePath) throws Exception {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Set<String>>>() {}.getType();
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, type);
        }
    }
    private static void writeMarkdownReport(List<String> reportLines) throws IOException {
        Path dir = Paths.get("target/coverage");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        Path filePath = dir.resolve("status-code-coverage-report.md");
        Files.write(filePath, reportLines);
        System.out.println("📄 状态码覆盖率报告已写入：" + filePath.toString());
    }
    private static void writeAllureEnvironment(double rate, int covered, int total) throws IOException {
        Path envFile = Paths.get("target/allure-results/environment.properties");

        Files.createDirectories(envFile.getParent());

        List<String> lines = Arrays.asList(
                String.format("状态码覆盖率=%.2f%%", rate * 100),
                "已覆盖状态码数=" + covered + "/" + total
        );
        Files.write(envFile, lines);
        System.out.println("📌 状态码覆盖率已写入 environment.properties");
    }

}

