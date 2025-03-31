🚩 Mall 接口自动化测试项目

本项目基于 Java + TestNG + HttpClient + Allure，实现对 mall 后台管理系统接口的自动化测试，涵盖用户登录、优选区商品查询等核心功能。

---

## 📁 项目结构说明
```
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
            ├── logback.xml  # 日志配置文件
│           └── testng.xml   # 灵活调整测试用例执行
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

## 📊 Allure 报告集成

本项目已集成 [Allure TestNG](https://docs.qameta.io/allure/) 报告框架，用于生成结构清晰、可视化的接口测试报告。

### ✅ 报告自动生成

执行测试后，Maven 会在以下目录中自动生成原始测试数据：

```
target/allure-results/
```

无需额外配置，执行以下命令即可：

```bash
mvn clean test
```

> `pom.xml` 中已配置了 `maven-surefire-plugin` 与 `testng.xml`，Allure 集成也已自动开启。

---

### ✅ 查看报告（本地）

请确保已安装 Allure 命令行工具：

```bash
brew install allure   # macOS
choco install allure  # Windows
```

然后执行：

```bash
allure serve target/allure-results
```

Allure 会自动生成 HTML 报告并打开浏览器预览。

报告用例详情（支持描述、参数、分组等）：
<img width="1434" alt="image" src="https://github.com/user-attachments/assets/e8cf51ad-d612-45ea-a89a-12bf37bd0369" />

异常日志附件
<img width="1428" alt="image" src="https://github.com/user-attachments/assets/27d5cb5b-2f8f-4f57-9815-6e24e4091ac3" />


---

### ✅ 环境信息自动展示

本项目支持自动将当前环境信息写入 Allure 报告首页的 "Environment" 标签页。

执行时将自动读取当前配置文件（如 `config-test.properties`）中的内容，并生成：

📄 `target/allure-results/environment.properties`：

```properties
env=test
baseUrl=http://60.204.173.174:8080
```

对应在报告首页展示为：

| Key     | Value                          |
|---------|-------------------------------|
| env     | test                           |
| baseUrl | http://60.204.173.174:8080     |


报告首页概览（包含用例数、环境变量等）：
![image](https://github.com/user-attachments/assets/866c5ca8-75c6-45e4-b249-c32a5636297a)

---

### 🛠 无需手动配置

- 报告环境信息由 `TestBase.java` 中自动生成  
- 所有环境字段统一维护在 `config-*.properties` 中  
- 仅需通过 `-Denv=test` 切换不同环境，无需关心 Allure 配置

---

## 🧪 用例组织与执行方式

本项目采用 **TestNG + Maven 插件** 方式组织接口自动化测试：

- 所有测试用例统一在 `testng.xml` 中按模块维护  
- `pom.xml` 中已配置自动加载，无需手动指定类或命令行参数

### ✅ 测试套件示例：`src/test/resources/testng.xml`

```xml
<suite name="Mall接口自动化测试套件">
  <parameter name="env" value="test" />

  <test name="用户模块">
    <classes>
      <class name="com.mycompany.mall.admin.business.user.LoginTest"/>
    </classes>
  </test>

  <test name="商品模块">
    <classes>
      <class name="com.mycompany.mall.admin.business.product.PreferenceAreaTest"/>
    </classes>
  </test>
</suite>
```

### ✅ 执行命令（已自动加载 testng.xml）

```bash
mvn clean test
```

> 💡 已支持多环境切换（如 `test/dev/uat`），详见下方环境配置章节。

---

## 🌍 环境配置说明

项目已支持根据不同环境自动加载配置，如接口地址、数据库连接等。

### ✅ 环境配置文件目录：

```
src/test/resources/
├── config-test.properties    # 测试环境
├── config-dev.properties     # 开发环境（可选）
├── config-uat.properties     # 预发布环境（可选）
```

### ✅ 配置示例（以 `config-test.properties` 为例）：

```properties
# 🔌 接口相关配置
baseUrl=http://60.204.173.174:8080

# 🛢 数据库连接配置
driverClassName=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://60.204.173.174:3306/mall?useSSL=false
username=reader
password=123456
initialSize=5
maxActive=20

# ✅ 添加用于展示到 Allure 的内容
env=test
```

### ✅ 切换环境方式：

通过 `-Denv=xxx` 控制当前使用的环境，默认为 `test`。

```bash
mvn clean test -Denv=test
```

或在 `pom.xml` 的 `systemPropertyVariables` 中默认配置：

```xml
<env>test</env>
```

---

## ✅ 整合说明：

- 测试用例中可通过 `Config.getBaseUrl()` 获取接口地址  
- 数据库操作统一通过 `DBUtil` 读取配置  
- 所有配置集中管理，无需硬编码任何 URL 或数据库参数

---


## 🧠 总结

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

---
