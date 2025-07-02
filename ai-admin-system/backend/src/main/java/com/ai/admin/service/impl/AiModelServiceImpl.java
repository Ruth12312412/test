package com.ai.admin.service.impl;

import com.ai.admin.entity.AiModel;
import com.ai.admin.mapper.AiModelMapper;
import com.ai.admin.service.AiModelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI模型服务实现
 */
@Service
public class AiModelServiceImpl extends ServiceImpl<AiModelMapper, AiModel> implements AiModelService {
    
    @Override
    public List<AiModel> getEnabledModels() {
        return this.list(new LambdaQueryWrapper<AiModel>()
                .eq(AiModel::getStatus, 1)
                .orderByAsc(AiModel::getSortOrder));
    }
    
    @Override
    public List<AiModel> getModelsByType(String modelType) {
        return this.list(new LambdaQueryWrapper<AiModel>()
                .eq(AiModel::getStatus, 1)
                .eq(AiModel::getModelType, modelType)
                .orderByAsc(AiModel::getSortOrder));
    }
}