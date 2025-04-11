import json
import os

SWAGGER_FILE = "target/coverage/swagger_status_codes.json"
TESTED_FILE = "target/coverage/tested_status_codes.json"
REPORT_FILE = "target/coverage/status_code_coverage_report.md"

def load_json(path):
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)

def main():
    swagger_map = load_json(SWAGGER_FILE)
    tested_map = load_json(TESTED_FILE)

    total_covered = 0
    total_defined = 0

    os.makedirs(os.path.dirname(REPORT_FILE), exist_ok=True)
    with open(REPORT_FILE, "w", encoding="utf-8") as f:
        f.write("# 接口状态码覆盖率报告\n\n")

        for api, defined_codes in swagger_map.items():
            tested_codes = tested_map.get(api, [])

            defined_set = set(defined_codes)
            tested_set = set(tested_codes)

            covered = defined_set & tested_set
            uncovered = defined_set - tested_set

            total_covered += len(covered)
            total_defined += len(defined_set)

            rate = (len(covered) / len(defined_set)) * 100 if defined_set else 0

            f.write(f"### {api}\n")
            f.write(f"- Swagger 定义状态码: {sorted(defined_set)}\n")
            f.write(f"- 测试触发状态码: {sorted(tested_set)}\n")
            f.write(f"- 未覆盖状态码: {sorted(uncovered)}\n")
            f.write(f"- 覆盖率: {rate:.2f}%\n\n")

        overall_rate = (total_covered / total_defined) * 100 if total_defined else 0
        f.write("---\n")
        f.write(f"✅ 总状态码覆盖率：{overall_rate:.2f}%（{total_covered}/{total_defined}）\n")

    print(f"✅ 状态码维度报告已生成：{REPORT_FILE}")

if __name__ == "__main__":
    main()
