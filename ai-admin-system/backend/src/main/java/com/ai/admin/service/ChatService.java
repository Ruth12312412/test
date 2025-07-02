package com.ai.admin.service;

import com.ai.admin.dto.ChatRequest;
import com.ai.admin.dto.ChatResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 聊天服务接口
 */
public interface ChatService {
    
    /**
     * 发送聊天消息
     */
    ChatResponse chat(ChatRequest request);
    
    /**
     * 流式聊天
     */
    SseEmitter streamChat(ChatRequest request);
}