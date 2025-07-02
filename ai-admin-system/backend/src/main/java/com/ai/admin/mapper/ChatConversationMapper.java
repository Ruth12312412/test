package com.ai.admin.mapper;

import com.ai.admin.entity.ChatConversation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对话记录Mapper
 */
@Mapper
public interface ChatConversationMapper extends BaseMapper<ChatConversation> {
}