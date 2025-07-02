package com.ai.admin.mapper;

import com.ai.admin.entity.ImageGeneration;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图片生成记录Mapper
 */
@Mapper
public interface ImageGenerationMapper extends BaseMapper<ImageGeneration> {
}