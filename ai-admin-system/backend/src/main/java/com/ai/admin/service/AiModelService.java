package com.ai.admin.service;

import com.ai.admin.entity.AiModel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * AI模型服务接口
 */
public interface AiModelService extends IService<AiModel> {
    
    /**
     * 获取启用的模型列表
     */
    List<AiModel> getEnabledModels();
    
    /**
     * 根据类型获取模型列表
     */
    List<AiModel> getModelsByType(String modelType);
}