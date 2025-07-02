package com.ai.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * API密钥实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("api_key")
public class ApiKey {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String keyName;
    
    private String apiKey;
    
    private String secretKey;
    
    private Integer status;
    
    private Long quotaLimit;
    
    private Long quotaUsed;
    
    private Integer rateLimit;
    
    private LocalDateTime expireTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}