import argparse
import subprocess
import os

def run_script(script_name):
    script_path = os.path.join("scripts", script_name)
    if not os.path.exists(script_path):
        print(f"❌ 脚本不存在: {script_path}")
        return
    print(f"\n🚀 正在执行：{script_name}")
    subprocess.run(["python3", script_path], check=True)

def main():
    parser = argparse.ArgumentParser(
        description="🧪 接口自动化测试覆盖率工具：支持 URL 和状态码维度分析"
    )
    parser.add_argument(
        "--type",
        choices=["all", "url", "status"],
        default="all",
        help="执行覆盖率分析类型: url=URL维度，status=状态码维度，all=全部执行（默认）"
    )
    args = parser.parse_args()

    steps = {
        "url": [
            "extract_tested_urls.py",
            "extract_swagger_urls.py",
            "compare_url_coverage.py"
        ],
        "status": [
            "extract_swagger_status_codes.py",
            "extract_tested_status_codes.py",
            "compare_status_code_coverage.py"
        ]
    }

    if args.type == "all":
        scripts_to_run = steps["url"] + steps["status"]
    else:
        scripts_to_run = steps[args.type]

    for script in scripts_to_run:
        run_script(script)

    print("\n✅ 所有步骤执行完成！覆盖率报告已输出至 target/coverage 目录。")

if __name__ == "__main__":
    main()
