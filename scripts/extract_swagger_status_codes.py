import requests
import json
import os

SWAGGER_JSON_URL = "http://60.204.173.174:8080/v2/api-docs"
OUTPUT_FILE = "target/coverage/swagger_status_codes.json"

def extract_status_codes():
    resp = requests.get(SWAGGER_JSON_URL, timeout=10)
    swagger = resp.json()

    paths = swagger.get("paths", {})
    result = {}

    for path, methods in paths.items():
        for method, detail in methods.items():
            key = f"{method.upper()} {path}"
            responses = detail.get("responses", {})
            result[key] = sorted(responses.keys())  # 状态码通常是字符串

    return result

if __name__ == "__main__":
    data = extract_status_codes()
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)

    print(f"✅ 已提取接口状态码定义，共 {len(data)} 条接口，保存至 {OUTPUT_FILE}")
