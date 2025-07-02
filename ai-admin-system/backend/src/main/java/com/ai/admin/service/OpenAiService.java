package com.ai.admin.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

/**
 * OpenAI服务接口
 */
public interface OpenAiService {

    /**
     * 聊天完成
     * @param modelName 模型名称
     * @param messages 消息列表
     * @param apiKey API密钥
     * @return 响应结果
     */
    String chatCompletion(String modelName, List<Map<String, String>> messages, String apiKey);

    /**
     * 流式聊天完成
     * @param modelName 模型名称
     * @param messages 消息列表
     * @param apiKey API密钥
     * @return SSE流
     */
    SseEmitter chatCompletionStream(String modelName, List<Map<String, String>> messages, String apiKey);

    /**
     * 生成图像
     * @param prompt 提示词
     * @param size 图像尺寸
     * @param apiKey API密钥
     * @return 响应结果
     */
    String generateImage(String prompt, String size, String apiKey);
}