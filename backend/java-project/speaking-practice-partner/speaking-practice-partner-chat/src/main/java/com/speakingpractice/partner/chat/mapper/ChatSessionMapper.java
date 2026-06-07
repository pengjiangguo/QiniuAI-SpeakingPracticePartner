package com.speakingpractice.partner.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.speakingpractice.partner.chat.domain.entity.ChatSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对话会话Mapper
 */
@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {

}