package com.mycompany.mall.admin.utils;

/**
 * @Author: Liu Yue
 * @Date: 2025/4/9 下午2:56
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class StatusCodeTestedCollector {

    public static String LOG_FILE = null;
    private static final String OUTPUT_FILE = "target/coverage/tested_status_codes.json";

    public static void main(String[] args) throws Exception {
        Map<String, Set<String>> statusMap = new LinkedHashMap<>();
        if (LOG_FILE == null) {
            throw new IllegalArgumentException("LOG_FILE 不能为空，请先赋值日志路径");
        }

        List<String> lines = Files.readAllLines(Paths.get(LOG_FILE));

        String currentInterface = null;

        Pattern reqPattern = Pattern.compile("请求地址: \\[(GET|POST|PUT|DELETE)\\] (.+)");
        Pattern codePattern = Pattern.compile("\"code\":\\s*(\\d{3})");

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();

            Matcher reqMatcher = reqPattern.matcher(line);
            Matcher codeMatcher = codePattern.matcher(line);

            if (reqMatcher.find()) {
                // 记录当前接口
                String method = reqMatcher.group(1);
                String path = reqMatcher.group(2);
                currentInterface = method + " " + path;
                statusMap.putIfAbsent(currentInterface, new TreeSet<>());
            } else if (codeMatcher.find() && currentInterface != null) {
                // 提取 code
                String code = codeMatcher.group(1);
                statusMap.get(currentInterface).add(code);
            }
        }

        // 写入 json 文件
        writeJson(statusMap);

        System.out.println("✅ 已生成 tested_status_codes.json，接口数量：" + statusMap.size());
    }

    private static void writeJson(Map<String, Set<String>> data) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(OUTPUT_FILE)) {
            gson.toJson(data, writer);
        }
    }
}

