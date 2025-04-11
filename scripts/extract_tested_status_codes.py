import os
import re
import json
from datetime import datetime
from collections import defaultdict

# ⏰ 自动识别当天日志文件
today = datetime.now().strftime("%Y-%m-%d")
LOG_FILE = f"target/logs/test-{today}.log"
OUTPUT_FILE = "target/coverage/tested_status_codes.json"

# ✅ 匹配格式：[POST] /admin/login
API_PATTERN = re.compile(r"请求地址:\s*\[(GET|POST|PUT|DELETE)\]\s+(/[^\s]+)")

# ✅ 匹配响应体中的 code 字段
RESP_PATTERN = re.compile(r'"code"\s*:\s*(\d{3})')

def extract_status_codes(log_path):
    result = defaultdict(set)
    current_api = None

    with open(log_path, "r", encoding="utf-8") as f:
        for line in f:
            api_match = API_PATTERN.search(line)
            if api_match:
                method, path = api_match.groups()
                current_api = f"{method} {path}"

            elif current_api:
                resp_match = RESP_PATTERN.search(line)
                if resp_match:
                    code = resp_match.group(1)
                    result[current_api].add(code)
                    current_api = None  # 防止与下一个请求串行干扰

    return {api: sorted(codes) for api, codes in result.items()}

if __name__ == "__main__":
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)

    if not os.path.exists(LOG_FILE):
        print(f"❌ 日志文件不存在: {LOG_FILE}")
    else:
        data = extract_status_codes(LOG_FILE)
        with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
            json.dump(data, f, indent=2, ensure_ascii=False)

        print(f"✅ 状态码提取完成，共 {len(data)} 个接口，保存至 {OUTPUT_FILE}")
