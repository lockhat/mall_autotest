import os
import re

TEST_DIR = "src/test/java"
OUTPUT_FILE = "target/coverage/tested_urls.txt"

# 方法映射：Java 方法 → HTTP Method
METHOD_MAPPING = {
    "doPostJson": "POST",
    "doPostForm": "POST",
    "doGet": "GET",
    "doGetWithStatus": "GET"
}

# 匹配变量赋值
VAR_ASSIGN_PATTERN = re.compile(r'String\s+(\w+)\s*=\s*"(/[^"]*)";')

# 匹配调用语句
CALL_PATTERN = re.compile(
    r'HttpClientUtil\.(do\w+)\(\s*Config\.getBaseUrl\(\)\s*\+\s*(\w+)', re.IGNORECASE)

url_variables = {}
found_urls = set()

# 扫描测试源码
for root, _, files in os.walk(TEST_DIR):
    for file in files:
        if file.endswith(".java"):
            with open(os.path.join(root, file), "r", encoding="utf-8") as f:
                lines = f.readlines()

                for line in lines:
                    # 收集路径变量
                    m = VAR_ASSIGN_PATTERN.search(line)
                    if m:
                        var_name, path = m.groups()
                        url_variables[var_name] = path

                for line in lines:
                    # 匹配调用 + 变量拼接
                    m = CALL_PATTERN.search(line)
                    if m:
                        java_method, var = m.groups()
                        http_method = METHOD_MAPPING.get(java_method)
                        path = url_variables.get(var)
                        if http_method and path:
                            found_urls.add(f"{http_method} {path}")

# 写入输出文件
os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
    for url in sorted(found_urls):
        f.write(url + "\n")

print(f"✅ 已提取 {len(found_urls)} 个接口（含 method），保存至 {OUTPUT_FILE}")
