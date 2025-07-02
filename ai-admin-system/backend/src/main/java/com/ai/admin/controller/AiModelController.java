package com.ai.admin.controller;

import com.ai.admin.common.Result;
import com.ai.admin.entity.AiModel;
import com.ai.admin.service.AiModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI模型控制器
 */
@Tag(name = "AI模型管理", description = "AI模型相关接口")
@RestController
@RequestMapping("/models")
public class AiModelController {
    
    @Autowired
    private AiModelService aiModelService;
    
    @Operation(summary = "获取所有启用的模型")
    @GetMapping("/enabled")
    public Result<List<AiModel>> getEnabledModels() {
        List<AiModel> models = aiModelService.getEnabledModels();
        return Result.success(models);
    }
    
    @Operation(summary = "根据类型获取模型")
    @GetMapping("/type/{modelType}")
    public Result<List<AiModel>> getModelsByType(@PathVariable String modelType) {
        List<AiModel> models = aiModelService.getModelsByType(modelType);
        return Result.success(models);
    }
    
    @Operation(summary = "获取所有模型")
    @GetMapping
    public Result<List<AiModel>> getAllModels() {
        List<AiModel> models = aiModelService.list();
        return Result.success(models);
    }
    
    @Operation(summary = "创建模型")
    @PostMapping
    public Result<String> createModel(@RequestBody AiModel model) {
        aiModelService.save(model);
        return Result.success("创建成功");
    }
    
    @Operation(summary = "更新模型")
    @PutMapping("/{id}")
    public Result<String> updateModel(@PathVariable Long id, @RequestBody AiModel model) {
        model.setId(id);
        aiModelService.updateById(model);
        return Result.success("更新成功");
    }
    
    @Operation(summary = "删除模型")
    @DeleteMapping("/{id}")
    public Result<String> deleteModel(@PathVariable Long id) {
        aiModelService.removeById(id);
        return Result.success("删除成功");
    }
}