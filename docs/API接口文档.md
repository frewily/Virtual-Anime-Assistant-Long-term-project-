# 二次元桌面助手 - API 接口文档

> 后端服务端口: 8080
> 基础路径: `/api`

---

## 目录

1. [系统状态接口](#1-系统状态接口)
2. [活动窗口接口](#2-活动窗口接口)
3. [语音合成接口](#3-语音合成接口)
4. [消息处理接口](#4-消息处理接口)
5. [虚拟形象接口](#5-虚拟形象接口)
6. [WebSocket 接口](#6-websocket-接口)

---

## 开发阶段与接口对应关系

> 采用 MVP 渐进式开发，每个阶段只实现必要的接口

| 阶段 | 目标 | 需实现的接口 | 状态 |
|------|------|--------------|------|
| **Phase 1** | MVP验证 | `GET /api/status` | 🔨 开发中 |
| **Phase 2** | 窗口监控 | `POST /api/report/window`<br>`GET /api/activity/current` | 📋 待开发 |
| **Phase 3** | 语音合成 | `POST /api/tts/speak`<br>`GET /api/tts/audio/{id}`<br>`GET /api/tts/voices` | 📋 待开发 |
| **Phase 4** | 智能响应 | `POST /api/message/trigger` | 📋 待开发 |
| **Phase 5** | 虚拟形象 | `WS /ws/avatar`<br>`GET /api/avatar/status`<br>`POST /api/avatar/action` | 📋 待开发 |
| **Phase 6** | QQ集成 | `POST /api/message` | 📋 待开发 |

### 开发原则

```
Phase 1 完成 → 实现 /api/status → 进入 Phase 2
Phase 2 完成 → 实现 /api/report/* → 进入 Phase 3
Phase 3 完成 → 实现 /api/tts/* → 进入 Phase 4
...以此类推
```

**每个阶段只做一件事，避免过度设计。**

---

## 1. 系统状态接口

### 1.1 获取系统状态

获取当前系统的 CPU、内存使用情况。

**请求**

```
GET /api/status
```

**响应**

```json
{
  "cpuUsage": "25.5%",
  "memoryUsed": "8.2GB",
  "memoryTotal": "16GB",
  "memoryUsage": "51.2%",
  "uptime": "2小时15分钟"
}
```

**字段说明**

| 字段 | 类型 | 说明 |
|------|------|------|
| cpuUsage | String | CPU 使用率百分比 |
| memoryUsed | String | 已使用内存 |
| memoryTotal | String | 总内存 |
| memoryUsage | String | 内存使用率百分比 |
| uptime | String | 系统运行时间 |

**实现提示**

```java
// 使用 OSHI 库获取系统信息
SystemInfo systemInfo = new SystemInfo();
CentralProcessor processor = systemInfo.getHardware().getProcessor();
GlobalMemory memory = systemInfo.getHardware().getMemory();

// CPU 使用率
double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;

// 内存信息
long totalMemory = memory.getTotal();
long availableMemory = memory.getAvailable();
long usedMemory = totalMemory - availableMemory;
```

---

## 2. 活动窗口接口

### 2.1 上报活动窗口

由 Python Agent 调用，上报当前活动窗口信息。

**请求**

```
POST /api/report/window
Content-Type: application/json
```

**请求体**

```json
{
  "appId": "vs_code",
  "windowTitle": "AssistantApplication.java — Visual Studio Code",
  "appName": "VS Code"
}
```

**字段说明**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| appId | String | 是 | 应用标识（小写下划线格式） |
| windowTitle | String | 是 | 窗口完整标题 |
| appName | String | 否 | 应用显示名称 |

**响应**

```
HTTP 200 OK
```

```json
{
  "success": true,
  "timestamp": "2024-01-15T14:30:00"
}
```

---

### 2.2 获取当前活动

查询当前正在使用的应用。

**请求**

```
GET /api/activity/current
```

**响应**

```json
{
  "appId": "vs_code",
  "windowTitle": "AssistantApplication.java — Visual Studio Code",
  "timestamp": "2024-01-15T14:30:00"
}
```

**字段说明**

| 字段 | 类型 | 说明 |
|------|------|------|
| appId | String | 应用标识 |
| windowTitle | String | 窗口标题 |
| timestamp | String | 最后更新时间 |

---

## 3. 语音合成接口

### 3.1 语音合成

将文字转换为语音。

**请求**

```
POST /api/tts/speak
Content-Type: application/json
```

**请求体**

```json
{
  "text": "主人，CPU使用率已经超过80%了哦！",
  "voiceId": "character_001",
  "fallbackVoice": "zh-CN-XiaoxiaoNeural"
}
```

**字段说明**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| text | String | 是 | 要合成的文字内容 |
| voiceId | String | 否 | GPT-SoVITS 自定义声线 ID |
| fallbackVoice | String | 否 | EdgeTTS 备用声线 |

**响应**

```json
{
  "success": true,
  "audioUrl": "/api/tts/audio/abc123.wav",
  "audioFormat": "wav",
  "duration": 3.5
}
```

**字段说明**

| 字段 | 类型 | 说明 |
|------|------|------|
| success | Boolean | 是否成功 |
| audioUrl | String | 音频文件 URL |
| audioFormat | String | 音频格式 |
| duration | Double | 音频时长（秒） |

---

### 3.2 获取音频文件

获取生成的音频文件。

**请求**

```
GET /api/tts/audio/{audioId}
```

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| audioId | String | 音频文件 ID |

**响应**

```
Content-Type: audio/wav
```

返回音频二进制流。

---

### 3.3 获取声线列表

获取所有可用的声线配置。

**请求**

```
GET /api/tts/voices
```

**响应**

```json
{
  "voices": [
    {
      "id": "character_001",
      "name": "小樱",
      "description": "活泼可爱的少女声线"
    },
    {
      "id": "character_002",
      "name": "小雪",
      "description": "温柔治愈的少女声线"
    }
  ],
  "default": "character_001"
}
```

---

## 4. 消息处理接口

### 4.1 发送消息

接收来自 QQ 或桌面的消息，进行处理并返回回复。

**请求**

```
POST /api/message
Content-Type: application/json
```

**请求体**

```json
{
  "source": "qq",
  "senderId": "12345678",
  "content": "今天CPU使用率怎么样？"
}
```

**字段说明**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| source | String | 是 | 来源：`qq` / `desktop` |
| senderId | String | 是 | 发送者标识 |
| content | String | 是 | 消息内容 |

**响应**

```json
{
  "reply": "今天平均 CPU 使用率 35%，最高达到 78%",
  "expression": "happy",
  "motion": "wave",
  "audioUrl": "/api/tts/audio/xyz789.wav"
}
```

**字段说明**

| 字段 | 类型 | 说明 |
|------|------|------|
| reply | String | 文字回复 |
| expression | String | Live2D 表情名称 |
| motion | String | Live2D 动作名称 |
| audioUrl | String | 语音音频 URL（可选） |

---

### 4.2 触发场景

手动触发一个场景事件。

**请求**

```
POST /api/message/trigger
Content-Type: application/json
```

**请求体**

```json
{
  "scenarioId": "high_cpu",
  "params": {
    "value": "85%"
  }
}
```

**响应**

```json
{
  "success": true,
  "reply": "主人，电脑好热啊，要不要休息一下？",
  "expression": "worried",
  "motion": "shake"
}
```

---

## 5. 虚拟形象接口

### 5.1 获取形象状态

获取当前 Live2D 形象的状态信息。

**请求**

```
GET /api/avatar/status
```

**响应**

```json
{
  "connected": true,
  "modelLoaded": true,
  "currentExpression": "idle",
  "lastActivity": "2024-01-15T14:30:00"
}
```

---

### 5.2 执行动作

让 Live2D 执行指定动作。

**请求**

```
POST /api/avatar/action
Content-Type: application/json
```

**请求体**

```json
{
  "expression": "happy",
  "motion": "wave"
}
```

**响应**

```json
{
  "success": true
}
```

---

## 6. WebSocket 接口

### 6.1 连接地址

```
ws://localhost:8080/ws/avatar
```

### 6.2 消息格式

所有消息均为 JSON 格式。

#### 服务端推送消息

**语音播放消息**

```json
{
  "type": "speak",
  "audioUrl": "/api/tts/audio/abc123.wav",
  "text": "主人，CPU使用率过高了！",
  "expression": "worried",
  "motion": "shake"
}
```

**动作消息**

```json
{
  "type": "action",
  "expression": "happy",
  "motion": "wave"
}
```

**状态更新消息**

```json
{
  "type": "status",
  "cpuUsage": "75%",
  "memoryUsage": "60%",
  "currentApp": "VS Code"
}
```

#### 客户端发送消息

**交互消息**

```json
{
  "type": "interaction",
  "action": "click",
  "x": 200,
  "y": 300
}
```

**聊天消息**

```json
{
  "type": "chat",
  "content": "你好呀"
}
```

---

## 附录

### A. 常见场景 ID

| ID | 名称 | 触发条件 |
|----|------|----------|
| `high_cpu` | 高负载警告 | CPU > 80% 持续 30 秒 |
| `gaming` | 摸鱼检测 | 工作时间打开游戏 |
| `late_night` | 深夜提醒 | 23:00 - 06:00 |
| `focus_mode` | 专注提醒 | IDE 使用超过 2 小时 |
| `idle` | 空闲状态 | 无操作 5 分钟 |

### B. Live2D 表情名称

| 名称 | 说明 |
|------|------|
| `idle` | 默认/待机 |
| `happy` | 开心 |
| `worried` | 担心 |
| `angry` | 生气 |
| `sleepy` | 困倦 |
| `curious` | 好奇 |

### C. Live2D 动作名称

| 名称 | 说明 |
|------|------|
| `idle` | 待机动作 |
| `wave` | 挥手 |
| `shake` | 摇头 |
| `point` | 指向 |
| `yawn` | 打哈欠 |
| `tap_body` | 点击身体 |

### D. EdgeTTS 可用声线

| 声线 ID | 名称 | 风格 |
|---------|------|------|
| `zh-CN-XiaoxiaoNeural` | 晓晓 | 活泼少女 |
| `zh-CN-XiaoyiNeural` | 晓伊 | 温柔少女 |
| `zh-CN-YunxiNeural` | 云希 | 阳光少年 |
| `zh-CN-YunjianNeural` | 云健 | 热血青年 |

---

## 错误响应

所有接口在发生错误时返回统一格式：

```json
{
  "success": false,
  "error": {
    "code": "TTS_SERVICE_UNAVAILABLE",
    "message": "GPT-SoVITS 服务不可用"
  }
}
```

**常见错误码**

| 错误码 | 说明 |
|--------|------|
| `INVALID_REQUEST` | 请求参数无效 |
| `TTS_SERVICE_UNAVAILABLE` | 语音服务不可用 |
| `VOICE_NOT_FOUND` | 声线不存在 |
| `SCENARIO_NOT_FOUND` | 场景不存在 |
| `INTERNAL_ERROR` | 内部错误 |

---

*文档版本: v1.1*
