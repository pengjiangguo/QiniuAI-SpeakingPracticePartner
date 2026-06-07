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
