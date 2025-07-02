package com.ai.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI模型实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_model")
public class AiModel {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String modelName;
    
    private String modelCode;
    
    private String modelType;
    
    private String provider;
    
    private String apiUrl;
    
    private String apiKey;
    
    private Integer maxTokens;
    
    private BigDecimal temperature;
    
    private BigDecimal priceInput;
    
    private BigDecimal priceOutput;
    
    private Integer status;
    
    private Integer sortOrder;
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}