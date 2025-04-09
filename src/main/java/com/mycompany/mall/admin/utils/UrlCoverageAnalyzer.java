package com.mycompany.mall.admin.utils;

/**
 * @Author: Liu Yue
 * @Date: 2025/4/9 上午10:53
 */

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class UrlCoverageAnalyzer {

    private static final String SWAGGER_JSON_URL = "http://60.204.173.174:8080/v2/api-docs";
    private static final String TESTED_URL_FILE = "target/coverage/tested_urls.txt";
    private static final String OUTPUT_MD_FILE = "target/coverage/url-coverage-report.md";

    public static void main(String[] args) throws Exception {
        // 1. 读取测试中调用过的 URL
        List<String> testedUrls = Files.readAllLines(Paths.get(TESTED_URL_FILE))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // 2. 获取 Swagger JSON
        String swaggerJson = getSwaggerJsonFromUrl(SWAGGER_JSON_URL);

        // 3. 解析路径列表
        JsonObject jsonObj = new Gson().fromJson(swaggerJson, JsonObject.class);
        JsonObject paths = jsonObj.getAsJsonObject("paths");
        Set<String> swaggerPaths = paths.keySet();

        Set<String> testedSet = new HashSet<>(testedUrls);
        Set<String> covered = new HashSet<>(swaggerPaths);
        covered.retainAll(testedSet);

        Set<String> uncovered = new HashSet<>(swaggerPaths);
        uncovered.removeAll(testedSet);

        // 4. 控制台输出
//        System.out.println("✅ Swagger 总接口数量：" + swaggerPaths.size());
//        System.out.println("✅ 已测试接口数量：" + covered.size());
//        System.out.println("⚠️  未测试接口数量：" + uncovered.size());
        double rate = 100.0 * covered.size() / swaggerPaths.size();
        System.out.printf("📊 覆盖率：%.2f%%\n", rate);

        // 5. 输出为 Markdown
        writeMarkdown(swaggerPaths.size(), covered.size(), uncovered);

        // 6. 构建失败判断（默认阈值：80%）
        double threshold = 80.0;
        if (rate < threshold) {
            System.err.printf("❌ 接口覆盖率低于阈值 %.2f%%，构建失败！\n", threshold);
            System.exit(1);
        } else {
            System.out.println("✅ 接口覆盖率符合要求！");
        }
    }

    private static void writeMarkdown(int total, int covered, Set<String> uncovered) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# 接口覆盖率报告");
        lines.add("");
        lines.add(String.format("- Swagger 总接口数量: **%d**", total));
        lines.add(String.format("- 已测试接口数量: **%d**", covered));
        lines.add(String.format("- 未测试接口数量: **%d**", uncovered.size()));
        lines.add(String.format("- 📊 接口覆盖率: **%.2f%%**", (100.0 * covered / total)));
        lines.add("");

        if (!uncovered.isEmpty()) {
            lines.add("## ❌ 未测试接口列表");
            List<String> sorted = new ArrayList<>(uncovered);
            Collections.sort(sorted);
            for (String url : sorted) {
                lines.add("- " + url);
            }
        }

        Path dir = Paths.get("target/coverage");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        Files.write(Paths.get(OUTPUT_MD_FILE), lines);
        System.out.println("📝 Markdown 报告已输出到: " + OUTPUT_MD_FILE);
    }

    private static String getSwaggerJsonFromUrl(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);

        try (InputStream inputStream = conn.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            return reader.lines().collect(Collectors.joining());
        }
    }
}
