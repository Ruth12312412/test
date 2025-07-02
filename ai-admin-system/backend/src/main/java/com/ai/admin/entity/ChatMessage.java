package com.ai.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 消息记录实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("chat_message")
public class ChatMessage {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long conversationId;
    
    private String role;
    
    private String content;
    
    private Integer tokens;
    
    private BigDecimal cost;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}