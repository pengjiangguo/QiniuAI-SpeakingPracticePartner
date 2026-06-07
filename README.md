# AI口语陪练桌面应用

## 项目介绍

AI口语陪练系统是一个基于人工智能技术的英语口语练习应用，提供实时语音识别、发音评测、语法纠错、词汇学习等功能。系统采用前后端分离架构，前端使用 Electron + Vue3 开发桌面应用，后端采用 Spring Boot 微服务架构和 Python AI 服务。

## 视频链接
- https://www.bilibili.com/video/BV1qrE868EKx/

### 核心功能

- **实时语音识别**：基于腾讯云 ASR 实现实时语音转文字
- **发音评测**：使用腾讯云口语评测 API 对用户发音进行实时评分
- **语法纠错**：基于 NLP 技术对用户口语进行语法分析和纠错
- **词汇学习**：自动识别生词并添加到生词本，支持词汇学习记录
- **场景对话**：提供多种对话场景，模拟真实英语对话环境
- **AI 对话**：集成 DeepSeek 大模型，提供智能对话回复
- **数据统计**：可视化展示学习进度、发音评分趋势等数据
- **语音合成**：使用腾讯云 TTS 将 AI 回复转换为语音播放

## 技术栈

### 前端技术栈

- **框架**：Vue 3.4 + Vite 5.0
- **UI 组件**：Element Plus 2.5
- **状态管理**：Pinia 2.1
- **路由**：Vue Router 4.2
- **图表**：ECharts 6.1
- **桌面应用**：Electron 42.0
- **HTTP 客户端**：Axios 1.6
- **WebSocket**：原生 WebSocket API

### 后端技术栈（Java）

- **框架**：Spring Boot 3.1.5
- **数据库**：MySQL 8.0
- **缓存**：Redis
- **ORM**：MyBatis-Plus
- **API 文档**：Knife4j（Swagger）
- **架构**：微服务多模块架构

### 后端技术栈（Python）

- **框架**：FastAPI 0.109
- **NLP**：spaCy 3.7、NLTK 3.8
- **语法检查**：language-tool-python 3.4
- **机器学习**：PyTorch 2.2、Transformers 4.37
- **音频处理**：librosa 0.10、soundfile 0.12
- **数据分析**：pandas 2.2、scikit-learn 1.4

## 项目结构

```
qiniuProject/
├── frontend/                          # 前端项目
│   ├── electron/                      # Electron 配置
│   │   ├── main.js                    # 主进程
│   │   └── preload.js                 # 预加载脚本
│   ├── src/
│   │   ├── api/                       # API 接口
│   │   ├── components/                # Vue 组件
│   │   ├── views/                     # 页面视图
│   │   ├── router/                    # 路由配置
│   │   ├── stores/                    # Pinia 状态管理
│   │   └── utils/                     # 工具类
│   ├── package.json
│   └── vite.config.js
│
├── backend/
│   ├── java-project/                  # Java 后端项目
│   │   └── speaking-practice-partner/
│   │       ├── speaking-practice-partner-web/          # Web 启动模块
│   │       ├── speaking-practice-partner-gateway/      # 网关模块
│   │       ├── speaking-practice-partner-auth/         # 认证模块
│   │       ├── speaking-practice-partner-user/         # 用户模块
│   │       ├── speaking-practice-partner-scene/        # 场景模块
│   │       ├── speaking-practice-partner-chat/         # 对话模块
│   │       ├── speaking-practice-partner-vocabulary/   # 词汇模块
│   │       ├── speaking-practice-partner-pronunciation/# 发音评测模块
│   │       ├── speaking-practice-partner-correction/   # 语法纠错模块
│   │       ├── speaking-practice-partner-statistics/   # 统计模块
│   │       ├── speaking-practice-partner-history/      # 历史记录模块
│   │       ├── speaking-practice-partner-common/       # 公共模块
│   │       ├── speaking-practice-partner-config/       # 配置模块
│   │       ├── speaking-practice-partner-infrastructure/# 基础设施模块
│   │       └── pom.xml
│   │
│   └── python-project/                # Python AI 服务
│       └── speaking-practice-partner/
│           ├── api/                   # API 路由
│           ├── services/              # 业务服务
│           │   ├── nlp/               # NLP 服务
│           │   ├── pronunciation/     # 发音评测
│           │   ├── analysis/          # 数据分析
│           │   └── recommendation/    # 推荐算法
│           ├── models/                # 数据模型
│           ├── utils/                 # 工具类
│           ├── main.py                # FastAPI 主入口
│           ├── config.py              # 配置文件
│           └── requirements.txt       # 依赖列表
│
└── README.md
```

## 环境要求

### 基础环境

- **Node.js**：>= 18.0
- **Java**：JDK 17
- **Python**：>= 3.9
- **MySQL**：>= 8.0
- **Redis**：>= 6.0
- **Maven**：>= 3.8

### 开发工具

- **IDE**：VS Code / IntelliJ IDEA / PyCharm
- **数据库工具**：Navicat / MySQL Workbench
- **API 测试**：Postman / Apifox

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd <project-name>
```

### 2. 数据库初始化

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE speaking_practice_partner CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入数据表
USE speaking_practice_partner;
SOURCE sqls/speaking_practice_partner.sql;
```

### 3. 前端启动

#### 3.1 安装依赖

```bash
cd frontend
npm install
```

#### 3.2 配置环境变量

`.env.development` 文件（默认即可）：

#### 3.3 启动开发服务器

**方式一：Electron 桌面应用模式**

```bash
npm run electron:dev
```

启动Electron 应用。

### 4. 后端启动（Java Spring Boot）

#### 4.1 配置数据库和 Redis

修改 `backend/java-project/speaking-practice-partner/speaking-practice-partner-web/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/speaking_practice_partner?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_mysql_password

  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
```

#### 4.2 配置 Python 服务地址

- 这里采用默认配置，无需修改。

```yaml
python:
  service:
    url: http://localhost:8000
```

#### 4.3 启动 Java 后端

**方式一：使用 Maven 命令**

```bash
cd backend/java-project/speaking-practice-partner
mvn clean install
cd speaking-practice-partner-web
mvn spring-boot:run
```

**方式二：使用 IDE**

1. 使用 IntelliJ IDEA 打开 `backend/java-project/speaking-practice-partner`
2. 等待 Maven 依赖下载完成
3. 找到 `SpeakingPracticePartnerApplication.java` 启动类
4. 右键 -> Run 'SpeakingPracticePartnerApplication'

启动成功后访问：
- **API 文档**：http://localhost:8080/sp/doc.html
- **接口路径**：http://localhost:8080/sp

### 5. 后端启动（Python AI 服务）

#### 5.1 创建虚拟环境

```bash
cd backend/python-project/speaking-practice-partner
python -m venv venv

# Windows
venv\Scripts\activate
```

- 或者打开PyCharm，选择 `backend/python-project/speaking-practice-partner` 作为项目目录。
> 1.创建本地虚拟环境


#### 5.2 安装依赖

```bash
pip install -r requirements.txt

# 安装本地 spaCy 模型包，在 model 目录下下载的模型包
pip install "model\en_core_web_sm-3.7.1-py3-none-any.whl"
```

- 下载language-tool服务
```bash
下载链接：
https://languagetool.org/download/LanguageTool-6.6.zip

解压到：
backend/python-project/speaking-practice-partner/tools/
```

#### 5.3 配置环境变量，无需修改

`.env` 文件（默认即可）：

```env
# 应用配置
APP_NAME=Speaking Practice Partner - AI Service
APP_VERSION=1.0.0
DEBUG=True
HOST=0.0.0.0
PORT=8000

# Java 服务地址
JAVA_SERVICE_URL=http://localhost:8080/sp/api

# 腾讯云配置
TENCENT_SECRET_ID=your_secret_id
TENCENT_SECRET_KEY=your_secret_key
TENCENT_ASR_APP_ID=your_app_id

# DeepSeek API 配置
DEEPSEEK_API_KEY=your_api_key
DEEPSEEK_BASE_URL=https://api.deepseek.com/v1

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_DB=1
```

### 启动language-tool服务
```bash
cmd

cd backend\python-project\speaking-practice-partner\tools\LanguageTool-6.6\LanguageTool-6.6

java -jar languagetool-server.jar --port 8081 --allow-origin "*"
```

#### 5.4 启动 Python 服务

**方式一：使用 uvicorn**

```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

**方式二：使用 run.py**

```bash
python run.py
```

启动成功后访问：
- **API 文档（Swagger UI）**：http://localhost:8000/docs
- **API 文档（ReDoc）**：http://localhost:8000/redoc
- **接口路径**：http://localhost:8000/api/v1

### 6. 完整启动流程

按以下顺序启动所有服务：

```bash
# 1. 启动 Redis
redis-server

# 2. 启动 MySQL（确保已运行）

# 3. 启动 Python AI 服务
1.
先启动language-tool服务

2.
cd backend/python-project/speaking-practice-partner
source venv/bin/activate  # Windows: venv\Scripts\activate
uvicorn main:app --reload --port 8000

# 4. 启动 Java 后端（新终端）
cd backend/java-project/speaking-practice-partner/speaking-practice-partner-web
mvn spring-boot:run

# 5. 启动前端（新终端）
cd frontend
npm run dev
# 或桌面应用模式
npm run electron:dev
```

## 配置说明

### 腾讯云配置

本项目使用腾讯云的以下服务：

1. **实时语音识别（ASR）**：用于实时语音转文字
2. **口语评测（SOE）**：用于发音评分
3. **语音合成（TTS）**：用于 AI 回复语音播放

配置步骤：
1. 访问 [腾讯云控制台](https://console.cloud.tencent.com/)
2. 开通相应服务
3. 获取 SecretId、SecretKey 和 AppId
4. 配置到前端和 Python 服务的环境变量中

### DeepSeek API 配置

用于 AI 智能对话功能：

1. 访问 [DeepSeek 官网](https://www.deepseek.com/)
2. 注册账号并获取 API Key
3. 配置到前端和 Python 服务的环境变量中

## API 文档

### Java 后端 API

访问：http://localhost:8080/sp/doc.html

主要接口：
- **用户模块**：注册、登录、个人信息管理
- **场景模块**：对话场景管理
- **对话模块**：对话记录、会话管理
- **词汇模块**：生词本、词汇学习记录
- **统计模块**：学习数据统计
- **历史模块**：对话历史记录

### Python AI 服务 API

访问：http://localhost:8000/docs

主要接口：
- **发音评测**：`/api/v1/pronunciation/evaluate`
- **语法纠错**：`/api/v1/grammar/correct`
- **数据分析**：`/api/v1/analysis/*`
- **推荐算法**：`/api/v1/recommendation/*`
- **音频处理**：`/api/v1/audio/*`

## 开发指南

### 前端开发

```bash
# 安装依赖
npm install

# 开发模式
npm run dev

# 构建生产版本
npm run build

# Electron 开发模式
npm run electron:dev

# Electron 打包
npm run electron:build
```

### Java 后端开发

```bash
# 编译项目
mvn clean install

# 运行测试
mvn test

# 打包
mvn package

# 运行
java -jar speaking-practice-partner-web/target/speaking-practice-partner-web-1.0.0-SNAPSHOT.jar
```

### Python 后端开发

```bash
# 安装开发依赖
pip install -r requirements.txt

# 运行开发服务器（自动重载）
uvicorn main:app --reload --port 8000

# 运行测试
pytest tests/
```

## 第三方库和框架说明

本项目使用了多个开源第三方库和框架，以下是详细说明：

### 前端第三方库

| 库名称 | 版本 | 用途 | 许可证 |
|--------|------|------|--------|
| Vue.js | 3.4.0 | 渐进式 JavaScript 框架，用于构建用户界面 | MIT |
| Vite | 5.0.0 | 下一代前端构建工具，提供快速的开发体验 | MIT |
| Element Plus | 2.5.0 | 基于 Vue 3 的组件库，提供丰富的 UI 组件 | MIT |
| Pinia | 2.1.7 | Vue 3 的状态管理库，用于管理应用状态 | MIT |
| Vue Router | 4.2.5 | Vue.js 官方路由管理器 | MIT |
| ECharts | 6.1.0 | 可视化图表库，用于数据可视化展示 | Apache-2.0 |
| Electron | 42.0.0 | 跨平台桌面应用框架，用于构建桌面应用 | MIT |
| Axios | 1.6.0 | HTTP 客户端，用于发送 HTTP 请求 | MIT |
| CryptoJS | 4.2.0 | JavaScript 加密库，用于生成签名 | MIT |
| Socket.io-client | 4.7.0 | WebSocket 客户端，用于实时通信 | MIT |

### Java 后端第三方库

| 库名称 | 版本 | 用途 | 许可证 |
|--------|------|------|--------|
| Spring Boot | 3.1.5 | 简化 Spring 应用开发的框架 | Apache-2.0 |
| Spring Cloud | - | 微服务框架，提供服务治理能力 | Apache-2.0 |
| Sa-Token | 1.37.0 | 轻量级 Java 权限认证框架，用于登录认证和权限控制 | Apache-2.0 |
| MyBatis-Plus | - | MyBatis 增强工具，简化数据库操作 | Apache-2.0 |
| MySQL Connector | 8.0 | MySQL 数据库驱动 | GPL-2.0 |
| Lettuce | - | Redis 客户端，用于 Redis 连接 | Apache-2.0 |
| Knife4j | - | Swagger 增强 UI，用于 API 文档展示 | Apache-2.0 |
| Lombok | - | Java 库，简化 Java 代码编写 | MIT |
| Jackson | - | JSON 序列化/反序列化库 | Apache-2.0 |
| Hutool | - | Java 工具类库，提供常用工具方法 | Apache-2.0 |

### Python 后端第三方库

| 库名称 | 版本 | 用途 | 许可证 |
|--------|------|------|--------|
| FastAPI | 0.109.0 | 现代 Web 框架，用于构建 API | MIT |
| Uvicorn | 0.27.0 | ASGI 服务器，用于运行 FastAPI | BSD-3-Clause |
| Pydantic | 2.5.3 | 数据验证库，用于数据模型验证 | MIT |
| spaCy | 3.7.4 | 工业级 NLP 库，用于自然语言处理 | MIT |
| NLTK | 3.8.1 | 自然语言工具包，用于 NLP 任务 | Apache-2.0 |
| language-tool-python | 3.4.0 | 语法检查库，用于英语语法纠错 | LGPL-3.0 |
| PyTorch | 2.2.0 | 深度学习框架，用于机器学习模型 | BSD-3-Clause |
| Transformers | 4.37.0 | Hugging Face 模型库，用于预训练模型 | Apache-2.0 |
| librosa | 0.10.1 | 音频处理库，用于音频分析 | ISC |
| soundfile | 0.12.1 | 音频文件读写库 | BSD-3-Clause |
| pandas | 2.2.0 | 数据分析库，用于数据处理 | BSD-3-Clause |
| scikit-learn | 1.4.0 | 机器学习库，用于模型训练和预测 | BSD-3-Clause |
| NumPy | 1.26.3 | 科学计算库，用于数值计算 | BSD-3-Clause |
| SciPy | 1.12.0 | 科学计算库，用于数学算法 | BSD-3-Clause |
| httpx | 0.26.0 | HTTP 客户端，支持异步请求 | BSD-3-Clause |
| aiohttp | 3.9.1 | 异步 HTTP 客户端/服务器 | Apache-2.0 |
| loguru | 0.7.2 | 日志库，用于应用日志记录 | MIT |
| python-dotenv | 1.0.0 | 环境变量管理库 | BSD-3-Clause |

### 云服务和 API

| 服务名称 | 提供商 | 用途 | 官网 |
|---------|--------|------|------|
| 实时语音识别（ASR） | 腾讯云 | 实时语音转文字 | https://cloud.tencent.com/product/asr |
| 口语评测（SOE） | 腾讯云 | 发音评分和评测 | https://cloud.tencent.com/product/soe |
| 语音合成（TTS） | 腾讯云 | 文字转语音 | https://cloud.tencent.com/product/tts |
| DeepSeek API | DeepSeek | AI 对话和大语言模型 | https://www.deepseek.com/ |

### 许可证说明

本项目使用的第三方库和框架主要采用以下开源许可证：

- **MIT License**：最宽松的开源许可证，允许商业使用
- **Apache-2.0**：允许商业使用，提供专利授权保护
- **BSD-3-Clause**：类似 MIT，允许商业使用
- **GPL-2.0**：严格的开源许可证，衍生作品必须开源
- **LGPL-3.0**：较宽松的 GPL，允许动态链接
- **ISC**：功能上等同于 MIT License

**重要提示**：
1. 使用本项目时，请遵守各第三方库的许可证要求
2. GPL 许可证的库（如 MySQL Connector）要求衍生作品开源
3. 商业使用前请仔细检查许可证兼容性
4. 部分库可能需要保留版权声明和许可证文本

## 常见问题

### 1. 前端无法连接后端

检查：
- 后端服务是否启动
- 前端 `.env.development` 中的 API 地址是否正确
- 是否存在跨域问题（后端已配置 CORS）

### 2. Python 服务启动失败

检查：
- Python 版本是否 >= 3.9
- 依赖是否全部安装：`pip install -r requirements.txt`
- spaCy 模型是否下载：`python -m spacy download en_core_web_sm`

### 3. 数据库连接失败

检查：
- MySQL 服务是否启动
- 数据库是否创建
- `application.yml` 中的数据库配置是否正确

### 4. Redis 连接失败

检查：
- Redis 服务是否启动
- `application.yml` 中的 Redis 配置是否正确

### 5. 语音识别/评测不工作

检查：
- 腾讯云配置是否正确
- 浏览器是否允许麦克风权限
- 是否使用 HTTPS 或 localhost（WebSocket 要求）

## 项目截图

（待补充）

## 贡献指南

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/your-feature`
3. 提交更改：`git commit -m 'Add some feature'`
4. 推送分支：`git push origin feature/your-feature`
5. 提交 Pull Request

## 许可证

MIT License

## 联系方式

- 项目团队：Speaking Practice Partner Team
- 实训营：七牛云 x XEngineer 暑假实训营

---

**注意**：本项目仅供学习和研究使用，请勿用于商业用途。
