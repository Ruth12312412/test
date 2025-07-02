package com.ai.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 图片生成记录实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("image_generation")
public class ImageGeneration {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String prompt;
    
    private String negativePrompt;
    
    private Long modelId;
    
    private Integer width;
    
    private Integer height;
    
    private Integer steps;
    
    private BigDecimal cfgScale;
    
    private Long seed;
    
    private String imageUrl;
    
    private Integer status;
    
    private String errorMsg;
    
    private BigDecimal cost;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}