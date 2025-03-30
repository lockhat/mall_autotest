## ✅ mall-autotest 项目 README.md

```markdown
# 🧪 Mall 接口自动化测试项目

本项目基于 Java + TestNG + HttpClient + Allure，实现对 mall 后台管理系统接口的自动化测试，涵盖用户登录、优选区商品查询等核心功能。

---

## 📁 项目结构说明
```bash

mall-autotest/
├── src/
│   ├── main/
│   │   ├── java/com/mycompany/mall/admin/
│   │   │   ├── base/         # 核心工具类：HTTP、DB、日志
│   │   │   ├── entity/       # 数据库实体类（如 UmsMember）
│   │   │   └── utils/        # 可复用工具类（如 JSON 解析等）
│   │   └── resources/        # 配置文件（如 Druid 配置）
│   └── test/
│       ├── java/com/mycompany/mall/admin/
│       │   ├── framework/    # 测试基类（TestBase）
│       │   └── testcase/     # 测试用例主目录（按模块划分）
│       │       ├── user/         # 用户模块（LoginTest）
│       │       └── product/      # 商品模块（PreferenceAreaTest）
│       └── resources/
│           ├── test-data/    # 测试数据 JSON / YAML
│           └── logback.xml   # 日志配置文件
├── pom.xml                  # Maven 构建配置
├── Jenkinsfile             # Jenkins 流水线配置
└── README.md               # 项目说明文档

```
---

## 🚀 快速开始

```bash
# 安装依赖 & 编译
mvn clean install

# 执行测试
mvn test

# 查看 Allure 报告（需安装 allure 命令行工具）
allure serve target/allure-results
```

---

## 🧩 核心模块说明

### ✅ 工具封装

| 类名               | 功能描述 |
|--------------------|----------|
| `HttpClientUtil`   | 使用 Apache HttpClient 封装 GET/POST 请求 |
| `HttpResponseWrapper` | 封装响应体与状态码，用于 GET/POST 请求结果处理 |
| `DBUtil`           | 使用 Druid + Apache DbUtils 实现数据库连接与查询封装 |
| `Log`              | 使用 SLF4J 封装日志生成器，统一日志输出入口 |

---

### ✅ 测试用例结构

| 类名                   | 模块说明 |
|------------------------|----------|
| `LoginTest.java`       | 用户登录接口：验证正确登录、用户名错误场景 |
| `PreferenceAreaTest.java` | 优选区接口：校验获取列表成功、未登录拦截等场景 |

---

### ✅ 测试基类

| 类名       | 说明 |
|------------|------|
| `TestBase` | 所有测试类的基类，提供 token 获取、测试数据加载等通用方法 |

---

## 🧾 测试数据示例（login.json）

路径：`src/test/resources/test-data/login.json`

```json
{
  "testUser": {
    "username": "admin",
    "password": "123456"
  },
  "testUserFailure": {
    "username": "wronguser",
    "password": "wrongpass"
  }
}
```

使用方法：

```java
JsonNode testData = getTestData("test-data/login.json", "testUser");
String json = objectMapper.writeValueAsString(testData);
```

---

## 📄 日志配置说明（logback.xml）

- 配置路径：`src/test/resources/logback.xml`
- 日志输出：控制台 + 每日文件（路径：`target/logs/test-YYYY-MM-DD.log`）
- 支持自动轮转，保留最近 7 天日志

---

## 📊 Allure 报告支持

- 测试类使用 `@Description` 和 `@Step` 注解描述测试步骤
- 可通过 `Allure.addAttachment(...)` 添加响应体、日志等内容
- 报告生成命令：

```bash
allure generate target/allure-results -o target/allure-report --clean
allure open target/allure-report
```

---

## 🧠 设计亮点总结

- 简洁封装：HTTP、DB、日志三大工具类解耦独立
- 测试清晰：业务分包、数据驱动、基类统一
- 报告可视化：结合 Allure 展示测试步骤与断言信息
- 可持续集成：集成 Jenkins 流水线配置，支持 CI 触发测试

---

## 🛠 技术栈

- Java 8+
- TestNG
- Apache HttpClient
- SLF4J + Logback
- Druid
- Jackson
- Allure 2.x
- Maven 3.6+

---

## 👨‍💻 作者

Liu Yue（刘悦）  
接口自动化 & 测试工程化实践者

---

## 📄 License

MIT License
```

---
