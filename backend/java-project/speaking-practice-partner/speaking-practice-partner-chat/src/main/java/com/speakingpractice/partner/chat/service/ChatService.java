package com.speakingpractice.partner.chat.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.speakingpractice.partner.chat.domain.dto.*;
import com.speakingpractice.partner.chat.domain.entity.ChatMessage;
import com.speakingpractice.partner.chat.domain.entity.ChatSession;
import com.speakingpractice.partner.chat.mapper.ChatMessageMapper;
import com.speakingpractice.partner.chat.mapper.ChatSessionMapper;
import com.speakingpractice.partner.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对话服务
 */
@Slf4j
@Service
public class ChatService {

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    /**
     * 创建对话会话
     */
    @Transactional(rollbackFor = Exception.class)
    public ChatSessionDTO createSession(CreateChatSessionRequest request) {
        String userId = StpUtil.getLoginIdAsString();

        // 创建会话实体
        ChatSession session = new ChatSession();
        BeanUtils.copyProperties(request, session);
        session.setUserId(userId);
        session.setStartTime(request.getStartTime() != null ? request.getStartTime() : LocalDateTime.now());
        session.setMessageCount(0);
        session.setStatus("ACTIVE");

        // 插入数据库
        chatSessionMapper.insert(session);

        return convertToSessionDTO(session);
    }

    /**
     * 添加对话消息
     */
    @Transactional(rollbackFor = Exception.class)
    public ChatMessageDTO addMessage(AddChatMessageRequest request) {
        String userId = StpUtil.getLoginIdAsString();

        // 查询会话
        ChatSession session = chatSessionMapper.selectById(request.getSessionId());
        if (session == null || session.getDeleted() == 1) {
            throw new BusinessException("会话不存在");
        }

        // 检查是否属于当前用户
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作该会话");
        }

        // 创建消息实体
        ChatMessage message = new ChatMessage();
        BeanUtils.copyProperties(request, message);

        // 插入数据库
        chatMessageMapper.insert(message);

        // 更新会话消息数量
        session.setMessageCount(session.getMessageCount() + 1);
        chatSessionMapper.updateById(session);

        return convertToMessageDTO(message);
    }

    /**
     * 结束对话会话
     */
    @Transactional(rollbackFor = Exception.class)
    public ChatSessionDTO endSession(String sessionId) {
        String userId = StpUtil.getLoginIdAsString();

        // 查询会话
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null || session.getDeleted() == 1) {
            throw new BusinessException("会话不存在");
        }

        // 检查是否属于当前用户
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作该会话");
        }

        // 更新会话状态
        session.setEndTime(LocalDateTime.now());
        session.setStatus("ENDED");

        // 计算对话时长
        if (session.getStartTime() != null) {
            long duration = java.time.Duration.between(session.getStartTime(), session.getEndTime()).getSeconds();
            session.setDurationSeconds((int) duration);
        }

        chatSessionMapper.updateById(session);

        return convertToSessionDTO(session);
    }

    /**
     * 更新会话评分
     */
    @Transactional(rollbackFor = Exception.class)
    public ChatSessionDTO updateSessionScore(String sessionId, UpdateChatSessionScoreRequest request) {
        String userId = StpUtil.getLoginIdAsString();

        // 查询会话
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null || session.getDeleted() == 1) {
            throw new BusinessException("会话不存在");
        }

        // 检查是否属于当前用户
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作该会话");
        }

        // 更新评分
        BeanUtils.copyProperties(request, session);
        chatSessionMapper.updateById(session);

        return convertToSessionDTO(session);
    }

    /**
     * 查询会话列表
     */
    public IPage<ChatSessionDTO> querySessions(ChatSessionQueryRequest request) {
        String userId = StpUtil.getLoginIdAsString();

        // 构建查询条件
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatSession::getUserId, userId)
                .eq(ChatSession::getDeleted, 0);

        // 场景筛选
        if (request.getSceneId() != null && !request.getSceneId().isEmpty()) {
            wrapper.eq(ChatSession::getSceneId, request.getSceneId());
        }

        // 状态筛选
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            wrapper.eq(ChatSession::getStatus, request.getStatus());
        }

        // 时间范围筛选
        if (request.getStartTimeFrom() != null && !request.getStartTimeFrom().isEmpty()) {
            wrapper.ge(ChatSession::getStartTime, LocalDateTime.parse(request.getStartTimeFrom()));
        }
        if (request.getStartTimeTo() != null && !request.getStartTimeTo().isEmpty()) {
            wrapper.le(ChatSession::getStartTime, LocalDateTime.parse(request.getStartTimeTo()));
        }

        // 排序：按开始时间倒序
        wrapper.orderByDesc(ChatSession::getStartTime);

        // 分页查询
        Page<ChatSession> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<ChatSession> sessionPage = chatSessionMapper.selectPage(page, wrapper);

        // 转换为DTO
        IPage<ChatSessionDTO> dtoPage = new Page<>(sessionPage.getCurrent(), sessionPage.getSize(), sessionPage.getTotal());
        List<ChatSessionDTO> dtoList = sessionPage.getRecords().stream()
                .map(this::convertToSessionDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /**
     * 查询会话详情
     */
    public ChatSessionDTO getSessionDetail(String sessionId) {
        String userId = StpUtil.getLoginIdAsString();

        // 查询会话
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null || session.getDeleted() == 1) {
            throw new BusinessException("会话不存在");
        }

        // 检查是否属于当前用户
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看该会话");
        }

        return convertToSessionDTO(session);
    }

    /**
     * 查询会话消息列表
     */
    public List<ChatMessageDTO> getSessionMessages(String sessionId) {
        String userId = StpUtil.getLoginIdAsString();

        // 查询会话
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null || session.getDeleted() == 1) {
            throw new BusinessException("会话不存在");
        }

        // 检查是否属于当前用户
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看该会话");
        }

        // 查询消息列表
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getSessionId, sessionId)
                .orderByAsc(ChatMessage::getCreatedAt);

        List<ChatMessage> messages = chatMessageMapper.selectList(wrapper);
        return messages.stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
    }

    /**
     * 删除会话
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSession(String sessionId) {
        String userId = StpUtil.getLoginIdAsString();

        // 查询会话
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null || session.getDeleted() == 1) {
            throw new BusinessException("会话不存在");
        }

        // 检查是否属于当前用户
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除该会话");
        }

        // 逻辑删除会话（使用 MyBatis-Plus 的 deleteById，会自动将 deleted 字段设置为 1）
        chatSessionMapper.deleteById(sessionId);
    }

    /**
     * 转换为SessionDTO
     */
    private ChatSessionDTO convertToSessionDTO(ChatSession session) {
        ChatSessionDTO dto = new ChatSessionDTO();
        BeanUtils.copyProperties(session, dto);
        return dto;
    }

    /**
     * 转换为MessageDTO
     */
    private ChatMessageDTO convertToMessageDTO(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        BeanUtils.copyProperties(message, dto);
        return dto;
    }

}