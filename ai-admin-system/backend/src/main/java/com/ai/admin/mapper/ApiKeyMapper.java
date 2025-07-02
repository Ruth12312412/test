package com.ai.admin.mapper;

import com.ai.admin.entity.ApiKey;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * API密钥Mapper
 */
@Mapper
public interface ApiKeyMapper extends BaseMapper<ApiKey> {
}