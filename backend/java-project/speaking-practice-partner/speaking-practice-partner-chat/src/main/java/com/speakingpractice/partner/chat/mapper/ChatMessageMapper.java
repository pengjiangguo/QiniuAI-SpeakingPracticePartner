package com.speakingpractice.partner.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.speakingpractice.partner.chat.domain.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对话消息Mapper
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

}