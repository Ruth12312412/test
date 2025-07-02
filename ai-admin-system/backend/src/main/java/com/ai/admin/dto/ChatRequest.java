package com.ai.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 聊天请求DTO
 */
@Data
public class ChatRequest {
    
    private String model;
    private List<Message> messages;
    private BigDecimal temperature;
    private Integer maxTokens;
    private Boolean stream;
    
    @Data
    public static class Message {
        private String role;
        private String content;
    }
}