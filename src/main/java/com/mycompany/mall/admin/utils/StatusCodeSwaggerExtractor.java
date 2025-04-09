package com.mycompany.mall.admin.utils;

/**
 * @Author: Liu Yue
 * @Date: 2025/4/9 下午1:56
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class StatusCodeSwaggerExtractor {

    private static final String SWAGGER_JSON_URL = "http://60.204.173.174:8080/v2/api-docs";
    private static final String OUTPUT_FILE = "target/coverage/swagger_status_codes.json";

    public static void main(String[] args) throws Exception {
        String json = getSwaggerJsonFromUrl(SWAGGER_JSON_URL);

        JsonObject swagger = new Gson().fromJson(json, JsonObject.class);
        JsonObject paths = swagger.getAsJsonObject("paths");

        Map<String, Set<String>> interfaceToCodes = new LinkedHashMap<>();

        for (Map.Entry<String, JsonElement> pathEntry : paths.entrySet()) {
            String path = pathEntry.getKey();
            JsonObject methods = pathEntry.getValue().getAsJsonObject();

            for (Map.Entry<String, JsonElement> methodEntry : methods.entrySet()) {
                String method = methodEntry.getKey().toUpperCase(); // GET, POST...
                JsonObject operation = methodEntry.getValue().getAsJsonObject();

                if (operation.has("responses")) {
                    JsonObject responses = operation.getAsJsonObject("responses");
                    Set<String> statusCodes = responses.keySet();
                    String key = method + " " + path;
                    interfaceToCodes.put(key, new TreeSet<>(statusCodes));
                }
            }
        }

        // 输出 JSON 文件
        writeJson(interfaceToCodes);

        System.out.println("✅ 已生成 swagger_status_codes.json，接口数量：" + interfaceToCodes.size());
    }

    private static void writeJson(Map<String, Set<String>> data) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(data);

        try (FileWriter writer = new FileWriter(OUTPUT_FILE)) {
            writer.write(jsonOutput);
        }
    }

    private static String getSwaggerJsonFromUrl(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);

        InputStream inputStream = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        reader.close();
        inputStream.close();
        conn.disconnect();

        return jsonBuilder.toString();
    }
}

