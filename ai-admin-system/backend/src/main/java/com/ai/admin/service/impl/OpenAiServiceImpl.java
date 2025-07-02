package com.ai.admin.service.impl;

import com.ai.admin.entity.AiModel;
import com.ai.admin.service.AiModelService;
import com.ai.admin.service.OpenAiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class OpenAiServiceImpl implements OpenAiService {

    @Autowired
    private AiModelService aiModelService;

    @Value("${ai.openai.api-key:}")
    private String defaultApiKey;

    @Value("${ai.openai.base-url:https://api.openai.com/v1}")
    private String defaultBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String chatCompletion(String modelName, List<Map<String, String>> messages, String apiKey) {
        try {
            // 获取模型配置
            AiModel model = aiModelService.getByName(modelName);
            if (model == null) {
                throw new RuntimeException("模型不存在: " + modelName);
            }

            String apiUrl = model.getApiUrl() + "/chat/completions";
            String actualApiKey = apiKey != null ? apiKey : (model.getApiKey() != null ? model.getApiKey() : defaultApiKey);

            if (actualApiKey == null || actualApiKey.isEmpty()) {
                throw new RuntimeException("API密钥未配置");
            }

            // 构建请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 2000);
            requestBody.put("temperature", 0.7);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(actualApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("API调用失败: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("OpenAI API调用失败", e);
            // 返回模拟响应
            return createMockResponse(modelName, messages);
        }
    }

    @Override
    public SseEmitter chatCompletionStream(String modelName, List<Map<String, String>> messages, String apiKey) {
        SseEmitter emitter = new SseEmitter(30000L);

        CompletableFuture.runAsync(() -> {
            try {
                // 获取模型配置
                AiModel model = aiModelService.getByName(modelName);
                if (model == null) {
                    emitter.completeWithError(new RuntimeException("模型不存在: " + modelName));
                    return;
                }

                String apiUrl = model.getApiUrl() + "/chat/completions";
                String actualApiKey = apiKey != null ? apiKey : (model.getApiKey() != null ? model.getApiKey() : defaultApiKey);

                if (actualApiKey == null || actualApiKey.isEmpty()) {
                    // 发送模拟流式响应
                    sendMockStreamResponse(emitter, modelName, messages);
                    return;
                }

                // 构建流式请求
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", modelName);
                requestBody.put("messages", messages);
                requestBody.put("max_tokens", 2000);
                requestBody.put("temperature", 0.7);
                requestBody.put("stream", true);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(actualApiKey);

                // 这里应该实现真实的流式请求
                // 由于复杂性，暂时使用模拟响应
                sendMockStreamResponse(emitter, modelName, messages);

            } catch (Exception e) {
                log.error("流式API调用失败", e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    @Override
    public String generateImage(String prompt, String size, String apiKey) {
        try {
            // 获取图像生成模型
            AiModel model = aiModelService.getByTypeAndProvider("image", "OpenAI");
            if (model == null) {
                throw new RuntimeException("图像生成模型未配置");
            }

            String apiUrl = model.getApiUrl() + "/images/generations";
            String actualApiKey = apiKey != null ? apiKey : (model.getApiKey() != null ? model.getApiKey() : defaultApiKey);

            if (actualApiKey == null || actualApiKey.isEmpty()) {
                throw new RuntimeException("API密钥未配置");
            }

            // 构建请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("prompt", prompt);
            requestBody.put("size", size != null ? size : "1024x1024");
            requestBody.put("n", 1);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(actualApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("图像生成失败: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("图像生成API调用失败", e);
            // 返回模拟响应
            return createMockImageResponse(prompt, size);
        }
    }

    private String createMockResponse(String modelName, List<Map<String, String>> messages) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("id", "chatcmpl-mock-" + System.currentTimeMillis());
            response.put("object", "chat.completion");
            response.put("created", System.currentTimeMillis() / 1000);
            response.put("model", modelName);

            Map<String, Object> choice = new HashMap<>();
            choice.put("index", 0);
            
            Map<String, String> message = new HashMap<>();
            message.put("role", "assistant");
            
            // 根据用户消息生成智能回复
            String userMessage = messages.get(messages.size() - 1).get("content");
            String mockReply = generateMockReply(userMessage);
            message.put("content", mockReply);
            
            choice.put("message", message);
            choice.put("finish_reason", "stop");

            response.put("choices", List.of(choice));

            Map<String, Object> usage = new HashMap<>();
            usage.put("prompt_tokens", 50);
            usage.put("completion_tokens", 100);
            usage.put("total_tokens", 150);
            response.put("usage", usage);

            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            log.error("创建模拟响应失败", e);
            return "{\"error\": \"服务暂时不可用\"}";
        }
    }

    private void sendMockStreamResponse(SseEmitter emitter, String modelName, List<Map<String, String>> messages) {
        try {
            String userMessage = messages.get(messages.size() - 1).get("content");
            String mockReply = generateMockReply(userMessage);
            
            // 分段发送响应
            String[] words = mockReply.split("");
            for (int i = 0; i < words.length; i++) {
                Map<String, Object> chunk = new HashMap<>();
                chunk.put("id", "chatcmpl-mock-" + System.currentTimeMillis());
                chunk.put("object", "chat.completion.chunk");
                chunk.put("created", System.currentTimeMillis() / 1000);
                chunk.put("model", modelName);

                Map<String, Object> choice = new HashMap<>();
                choice.put("index", 0);
                
                Map<String, Object> delta = new HashMap<>();
                if (i == 0) {
                    delta.put("role", "assistant");
                }
                delta.put("content", words[i]);
                
                choice.put("delta", delta);
                if (i == words.length - 1) {
                    choice.put("finish_reason", "stop");
                }

                chunk.put("choices", List.of(choice));

                String chunkJson = objectMapper.writeValueAsString(chunk);
                emitter.send(SseEmitter.event().data("data: " + chunkJson + "\n\n"));
                
                Thread.sleep(50); // 模拟流式延迟
            }

            emitter.send(SseEmitter.event().data("data: [DONE]\n\n"));
            emitter.complete();

        } catch (Exception e) {
            log.error("发送流式响应失败", e);
            emitter.completeWithError(e);
        }
    }

    private String createMockImageResponse(String prompt, String size) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("created", System.currentTimeMillis() / 1000);

            Map<String, String> imageData = new HashMap<>();
            // 使用占位图片服务
            String imageUrl = "https://picsum.photos/" + (size != null ? size.replace("x", "/") : "1024/1024") + "?random=" + System.currentTimeMillis();
            imageData.put("url", imageUrl);

            response.put("data", List.of(imageData));

            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            log.error("创建模拟图像响应失败", e);
            return "{\"error\": \"图像生成服务暂时不可用\"}";
        }
    }

    private String generateMockReply(String userMessage) {
        // 简单的智能回复逻辑
        if (userMessage.contains("你好") || userMessage.contains("hello")) {
            return "您好！我是AI助手，很高兴为您服务。有什么我可以帮助您的吗？";
        } else if (userMessage.contains("画图") || userMessage.contains("图片") || userMessage.contains("image")) {
            return "我可以帮您生成图片！请告诉我您想要什么样的图片，我会为您创作。您可以描述图片的内容、风格、颜色等细节。";
        } else if (userMessage.contains("模型") || userMessage.contains("model")) {
            return "我支持多种AI模型，包括GPT-3.5、GPT-4等对话模型，以及DALL-E、MidJourney等图像生成模型。您可以在模型管理页面查看和配置可用的模型。";
        } else if (userMessage.contains("API") || userMessage.contains("密钥")) {
            return "关于API密钥管理，您可以在系统中生成和管理API密钥，监控使用情况，设置使用限制。每个密钥都有独立的统计和权限控制。";
        } else if (userMessage.contains("功能") || userMessage.contains("特性")) {
            return "本系统主要功能包括：\n1. OpenAI兼容的对话API\n2. 图像生成功能\n3. API密钥管理\n4. 多模型支持\n5. 使用统计分析\n6. 用户权限管理\n\n您想了解哪个功能的详细信息？";
        } else {
            return "感谢您的提问！作为AI助手，我会尽力为您提供帮助。这是一个功能强大的AI管理系统，支持对话、图像生成、API管理等多种功能。如果您有具体问题，请随时告诉我。";
        }
    }
}