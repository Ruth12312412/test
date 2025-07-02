package com.ai.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 对话记录实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("chat_conversation")
public class ChatConversation {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String title;
    
    private Long modelId;
    
    private Long totalTokens;
    
    private BigDecimal totalCost;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}