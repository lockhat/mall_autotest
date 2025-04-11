import requests
import os

# ✅ Swagger JSON 地址（可按需替换）
SWAGGER_JSON_URL = "http://60.204.173.174:8080/v2/api-docs"
OUTPUT_FILE = "target/coverage/swagger_urls.txt"

def extract_swagger_urls(swagger_url):
    response = requests.get(swagger_url, timeout=10)
    swagger = response.json()

    paths = swagger.get("paths", {})
    result = []

    for path, methods in paths.items():
        for method in methods.keys():
            result.append(f"{method.upper()} {path}")

    return sorted(result)

if __name__ == "__main__":
    urls = extract_swagger_urls(SWAGGER_JSON_URL)

    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        for url in urls:
            f.write(url + "\n")

    print(f"✅ Swagger 接口数量：{len(urls)}，已写入 {OUTPUT_FILE}")
