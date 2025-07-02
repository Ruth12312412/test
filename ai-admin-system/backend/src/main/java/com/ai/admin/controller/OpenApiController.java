package com.ai.admin.controller;

import com.ai.admin.dto.ChatRequest;
import com.ai.admin.dto.ChatResponse;
import com.ai.admin.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * OpenAPI兼容控制器
 */
@Tag(name = "OpenAPI兼容接口", description = "兼容OpenAI API格式的接口")
@RestController
@RequestMapping("/openapi/v1")
public class OpenApiController {
    
    @Autowired
    private ChatService chatService;
    
    @Operation(summary = "聊天完成接口")
    @PostMapping("/chat/completions")
    public Object chatCompletions(@RequestBody ChatRequest request) {
        if (Boolean.TRUE.equals(request.getStream())) {
            return streamChatCompletions(request);
        } else {
            return chatService.chat(request);
        }
    }
    
    @Operation(summary = "流式聊天完成接口")
    @PostMapping(value = "/chat/completions/stream", produces = "text/event-stream")
    public SseEmitter streamChatCompletions(@RequestBody ChatRequest request) {
        return chatService.streamChat(request);
    }
}