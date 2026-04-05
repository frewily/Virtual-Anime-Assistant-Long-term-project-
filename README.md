# 二次元桌面助手

基于 Java + Spring Boot 的兴趣驱动学习项目，打造一个能"看懂"你在做什么的二次元桌面助手。

## 功能特性

- 监控电脑状态（CPU/内存/当前窗口）
- 语音播报与提醒（GPT-SoVITS 自定义声线）
- 即时通讯机器人交互（QQ/钉钉）
- Live2D 虚拟形象展示
- 多端同步交互

## 项目结构

```
二次元桌面助手/
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
│   │   ├── preload.js     # 预加载脚本
│   │   └── renderer/      # 渲染进程
│   └── package.json
│
├── agent/             # Python 数据采集 Agent
│   ├── window_monitor.py  # 窗口监控
│   ├── app_identifier.py  # 应用识别
│   ├── reporter.py        # 数据上报
│   └── requirements.txt
│
├── config/            # 配置文件
│   ├── voices.yml         # 声线配置
│   ├── scenarios.yml      # 场景规则
│   └── replies.yml        # 回复模板
│
└── docs/              # 文档
    ├── 项目纲要.md         # 完整纲要
    └── 项目纲要_精简版.md  # 精简纲要
```

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- Python 3.10+
- Docker（GPT-SoVITS 部署，可选）

## 快速开始

### 1. 启动后端

```bash
cd backend
mvn spring-boot:run
```

### 2. 启动 Agent

```bash
cd agent
pip install -r requirements.txt
python window_monitor.py
```

### 3. 启动桌面应用

```bash
cd desktop-app
npm install
npm start
```

### 4. 验证

- 浏览器访问: http://localhost:8080/api/status
- 桌面应用显示 Live2D 角色

## 核心接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/status` | GET | 获取系统状态 |
| `/api/report/window` | POST | 上报活动窗口 |
| `/api/tts/speak` | POST | 语音合成 |
| `/api/activity/current` | GET | 查询当前活动 |
| `/ws/avatar` | WS | Live2D WebSocket |

## 开发阶段

| 阶段 | 目标 | 状态 |
|------|------|------|
| Phase 1 | MVP验证 | 待开发 |
| Phase 2 | 窗口监控 | 待开发 |
| Phase 3 | 语音合成 | 待开发 |
| Phase 4 | 智能响应 | 待开发 |
| Phase 5 | 虚拟形象 | 待开发 |
| Phase 6 | QQ集成 | 待开发 |

## 文档

- [完整项目纲要](docs/项目纲要.md)
- [精简版纲要](docs/项目纲要_精简版.md)

## License

MIT
