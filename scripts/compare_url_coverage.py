import os

SWAGGER_FILE = "target/coverage/swagger_urls.txt"
TESTED_FILE = "target/coverage/tested_urls.txt"
REPORT_FILE = "target/coverage/url_coverage_report.md"

def read_lines(path):
    with open(path, "r", encoding="utf-8") as f:
        return set(line.strip() for line in f if line.strip())

def main():
    swagger_urls = read_lines(SWAGGER_FILE)
    tested_urls = read_lines(TESTED_FILE)

    covered = swagger_urls & tested_urls
    uncovered = swagger_urls - tested_urls

    total = len(swagger_urls)
    hit = len(covered)
    rate = (hit / total * 100) if total else 0

    os.makedirs(os.path.dirname(REPORT_FILE), exist_ok=True)
    with open(REPORT_FILE, "w", encoding="utf-8") as f:
        f.write("# 接口 URL 覆盖率报告\n\n")
        f.write(f"✅ Swagger 接口总数: {total}\n")
        f.write(f"✅ 已测试接口数: {hit}\n")
        f.write(f"⚠️ 未测试接口数: {len(uncovered)}\n")
        f.write(f"📊 URL 覆盖率: {rate:.2f}%\n\n")

        if uncovered:
            f.write("## 未覆盖的接口列表：\n\n")
            for item in sorted(uncovered):
                f.write(f"- {item}\n")

    print(f"✅ URL 覆盖率报告已生成: {REPORT_FILE}")

if __name__ == "__main__":
    main()
