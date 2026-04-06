package com.assistant.config;

import org.springframework.context.annotation.Configuration;

/**
 * 声线配置类
 * 
 * <p>管理 GPT-SoVITS 和 EdgeTTS 的声线配置。</p>
 * 
 * <p>实现阶段：Phase 3 (语音合成)</p>
 * 
 * <p>核心功能：</p>
 * <ul>
 *   <li>加载 voices.yml 配置文件</li>
 *   <li>管理多个声线配置</li>
 *   <li>提供声线查询接口</li>
 * </ul>
 * 
 * <p>配置文件结构（config/voices.yml）：</p>
 * <pre>
 * voices:
 *   - id: character_001
 *     name: 小樱
 *     description: 活泼可爱的少女声线
 *     referenceAudio: character_001.wav
 *     promptText: 大家好，我是小樱，很高兴认识你们！
 *     defaultParams:
 *       top_k: 5
 *       top_p: 1.0
 *       temperature: 1.0
 *       
 *   - id: character_002
 *     name: 小雪
 *     description: 温柔治愈的少女声线
 *     referenceAudio: character_002.wav
 *     promptText: 你好呀，今天过得怎么样？
 *     defaultParams:
 *       top_k: 5
 *       top_p: 1.0
 *       temperature: 1.0
 * 
 * default:
 *   voiceId: character_001
 *   fallbackVoice: zh-CN-XiaoxiaoNeural
 * </pre>
 * 
 * <p>声线切换流程：</p>
 * <pre>
 * 用户请求语音合成
 *     ↓
 * VoiceConfig.getVoice(voiceId)
 *     ↓
 * 返回声线配置（referenceAudio + promptText）
 *     ↓
 * TTSService 调用 GPT-SoVITS
 * </pre>
 * 
 * <p>后续扩展：</p>
 * <ul>
 *   <li>支持动态添加声线</li>
 *   <li>支持声线预览</li>
 *   <li>支持声线参数调优</li>
 * </ul>
 * 
 * @author Assistant
 * @version 1.0
 * @since Phase 3
 * @see com.assistant.service.TTSService
 */
@Configuration
public class VoiceConfig {

    // TODO: Phase 3 实现
    // 1. 使用 @ConfigurationProperties 加载 voices.yml
    // 2. 提供 getVoice(String id) 方法
    // 3. 提供 getDefaultVoice() 方法
    // 4. 提供 getAllVoices() 方法

}
