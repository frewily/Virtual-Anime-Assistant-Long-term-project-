# Virtual Anime Assistant (二次元桌面助手)

> 🚧 **长期开发项目** - 目前处于刚开发阶段

基于 Java + Spring Boot 的兴趣驱动学习项目，打造一个能"看懂"你在做什么的二次元桌面助手。

## 项目状态

| 状态 | 说明 |
|------|------|
| 🔨 开发中 | 项目刚刚启动，正在搭建基础框架 |

## 功能特性（规划中）

- 🖥️ 监控电脑状态（CPU/内存/当前窗口）
- 🎙️ 语音播报与提醒（GPT-SoVITS 自定义声线）
- 💬 即时通讯机器人交互（QQ/钉钉）
- 🎭 Live2D 虚拟形象展示
- 🔄 多端同步交互

## 技术栈

| 模块 | 技术 |
|------|------|
| 后端 | Java 17 + Spring Boot 3.x |
| 系统监控 | OSHI |
| 窗口监控 | Python + pywin32 |
| 语音合成 | GPT-SoVITS / EdgeTTS |
| 虚拟形象 | Electron + Live2D |
| QQ机器人 | NapCatQQ (OneBot 11) |

## 项目结构

```
Virtual-Anime-Assistant-Long-term-project-/
├── backend/           # Java Spring Boot 后端
│   ├── src/main/java/com/assistant/
│   │   ├── controller/    # REST API 控制器
│   │   ├── service/       # 业务逻辑层
│   │   ├── model/         # 数据模型
│   │   ├── websocket/     # WebSocket 处理
│   │   └── config/        # 配置类
│   └── pom.xml
│
├── desktop-app/       # Electron 桌面应用 (Live2D)
│   ├── src/
│   │   ├── main.js        # Electron 主进程
│   │   └── renderer/      # 渲染进程
│   └── package.json
│
├── agent/             # Python 数据采集 Agent
│   ├── window_monitor.py  # 窗口监控
│   ├── app_identifier.py  # 应用识别
│   └── reporter.py        # 数据上报
│
├── config/            # 配置文件
│   ├── voices.yml         # 声线配置
│   ├── scenarios.yml      # 场景规则
│   └── replies.yml        # 回复模板
│
└── docs/              # 文档
    ├── 项目纲要.md
    ├── 项目纲要_精简版.md
    └── API接口文档.md
```

## 开发阶段

| 阶段 | 目标 | 状态 |
|------|------|------|
| Phase 1 | MVP验证 - 系统状态接口 | 🔨 开发中 |
| Phase 2 | 窗口监控 Agent | 📋 待开发 |
| Phase 3 | 语音合成 (GPT-SoVITS) | 📋 待开发 |
| Phase 4 | 智能响应引擎 | 📋 待开发 |
| Phase 5 | Live2D 虚拟形象 | 📋 待开发 |
| Phase 6 | QQ 机器人集成 | 📋 待开发 |
| Phase 7 | Web 管理界面 | 📋 可选 |

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- Python 3.10+
- Docker（GPT-SoVITS 部署，可选）

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/frewily/Virtual-Anime-Assistant-Long-term-project-.git
cd Virtual-Anime-Assistant-Long-term-project-
```

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

### 3. 启动 Agent

```bash
cd agent
pip install -r requirements.txt
python window_monitor.py
```

### 4. 启动桌面应用

```bash
cd desktop-app
npm install
npm start
```

### 5. 验证

- 浏览器访问: http://localhost:8080/api/status
- 桌面应用显示 Live2D 角色

## 文档

- [完整项目纲要](docs/项目纲要.md) - 详细的技术方案和实现思路
- [精简版纲要](docs/项目纲要_精简版.md) - 快速了解项目
- [API接口文档](docs/API接口文档.md) - 后端接口说明

## 贡献

这是一个兴趣驱动的学习项目，欢迎交流讨论！

## License

MIT

---

*最后更新: 2024年*
