package com.ai.admin.service.impl;

import com.ai.admin.dto.ChatRequest;
import com.ai.admin.dto.ChatResponse;
import com.ai.admin.entity.AiModel;
import com.ai.admin.service.AiModelService;
import com.ai.admin.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 聊天服务实现
 */
@Service
public class ChatServiceImpl implements ChatService {
    
    @Autowired
    private AiModelService aiModelService;
    
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public ChatResponse chat(ChatRequest request) {
        try {
            // 获取模型配置
            AiModel model = aiModelService.getById(Long.parseLong(request.getModel()));
            if (model == null) {
                throw new RuntimeException("模型不存在");
            }
            
            // 构建请求
            RequestBody requestBody = RequestBody.create(
                    objectMapper.writeValueAsString(request),
                    MediaType.get("application/json")
            );
            
            Request httpRequest = new Request.Builder()
                    .url(model.getApiUrl())
                    .addHeader("Authorization", "Bearer " + model.getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();
            
            // 发送请求
            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("API调用失败: " + response.code());
                }
                
                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, ChatResponse.class);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("聊天服务异常: " + e.getMessage(), e);
        }
    }
    
    @Override
    public SseEmitter streamChat(ChatRequest request) {
        SseEmitter emitter = new SseEmitter(30000L);
        
        CompletableFuture.runAsync(() -> {
            try {
                // 获取模型配置
                AiModel model = aiModelService.getById(Long.parseLong(request.getModel()));
                if (model == null) {
                    emitter.completeWithError(new RuntimeException("模型不存在"));
                    return;
                }
                
                // 设置流式请求
                request.setStream(true);
                
                // 构建请求
                RequestBody requestBody = RequestBody.create(
                        objectMapper.writeValueAsString(request),
                        MediaType.get("application/json")
                );
                
                Request httpRequest = new Request.Builder()
                        .url(model.getApiUrl())
                        .addHeader("Authorization", "Bearer " + model.getApiKey())
                        .addHeader("Content-Type", "application/json")
                        .post(requestBody)
                        .build();
                
                // 发送流式请求
                try (Response response = httpClient.newCall(httpRequest).execute()) {
                    if (!response.isSuccessful()) {
                        emitter.completeWithError(new RuntimeException("API调用失败: " + response.code()));
                        return;
                    }
                    
                    // 处理流式响应
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        String[] lines = responseBody.string().split("\n");
                        for (String line : lines) {
                            if (line.startsWith("data: ")) {
                                String data = line.substring(6);
                                if (!"[DONE]".equals(data)) {
                                    emitter.send(SseEmitter.event().data(data));
                                }
                            }
                        }
                    }
                    
                    emitter.complete();
                }
                
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }
}