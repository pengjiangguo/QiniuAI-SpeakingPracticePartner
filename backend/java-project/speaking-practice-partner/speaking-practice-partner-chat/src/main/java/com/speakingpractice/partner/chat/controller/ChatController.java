package com.speakingpractice.partner.chat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.speakingpractice.partner.chat.domain.dto.*;
import com.speakingpractice.partner.chat.service.ChatService;
import com.speakingpractice.partner.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 对话控制器
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@Tag(name = "对话管理", description = "对话会话和消息管理接口")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 创建对话会话
     */
    @PostMapping("/session/create")
    @Operation(summary = "创建对话会话", description = "创建新的对话会话")
    public Result<ChatSessionDTO> createSession(@Validated @RequestBody CreateChatSessionRequest request) {
        try {
            ChatSessionDTO session = chatService.createSession(request);
            return Result.success("创建会话成功", session);
        } catch (Exception e) {
            log.error("创建会话失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 添加对话消息
     */
    @PostMapping("/message/add")
    @Operation(summary = "添加对话消息", description = "向会话中添加新的对话消息")
    public Result<ChatMessageDTO> addMessage(@Validated @RequestBody AddChatMessageRequest request) {
        try {
            ChatMessageDTO message = chatService.addMessage(request);
            return Result.success("添加消息成功", message);
        } catch (Exception e) {
            log.error("添加消息失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 结束对话会话
     */
    @PutMapping("/session/{sessionId}/end")
    @Operation(summary = "结束对话会话", description = "结束对话会话并计算时长")
    public Result<ChatSessionDTO> endSession(@PathVariable String sessionId) {
        try {
            ChatSessionDTO session = chatService.endSession(sessionId);
            return Result.success("结束会话成功", session);
        } catch (Exception e) {
            log.error("结束会话失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 更新会话评分
     */
    @PutMapping("/session/{sessionId}/score")
    @Operation(summary = "更新会话评分", description = "更新对话会话的各项评分")
    public Result<ChatSessionDTO> updateSessionScore(
            @PathVariable String sessionId,
            @Validated @RequestBody UpdateChatSessionScoreRequest request) {
        try {
            ChatSessionDTO session = chatService.updateSessionScore(sessionId, request);
            return Result.success("更新评分成功", session);
        } catch (Exception e) {
            log.error("更新评分失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 查询会话列表
     */
    @GetMapping("/session/list")
    @Operation(summary = "查询会话列表", description = "分页查询用户的对话会话列表")
    public Result<IPage<ChatSessionDTO>> querySessions(ChatSessionQueryRequest request) {
        try {
            IPage<ChatSessionDTO> page = chatService.querySessions(request);
            return Result.success(page);
        } catch (Exception e) {
            log.error("查询会话列表失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 查询会话详情
     */
    @GetMapping("/session/{sessionId}")
    @Operation(summary = "查询会话详情", description = "获取对话会话的详细信息")
    public Result<ChatSessionDTO> getSessionDetail(@PathVariable String sessionId) {
        try {
            ChatSessionDTO session = chatService.getSessionDetail(sessionId);
            return Result.success(session);
        } catch (Exception e) {
            log.error("查询会话详情失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 查询会话消息列表
     */
    @GetMapping("/session/{sessionId}/messages")
    @Operation(summary = "查询会话消息列表", description = "获取对话会话的所有消息")
    public Result<List<ChatMessageDTO>> getSessionMessages(@PathVariable String sessionId) {
        try {
            List<ChatMessageDTO> messages = chatService.getSessionMessages(sessionId);
            return Result.success(messages);
        } catch (Exception e) {
            log.error("查询会话消息列表失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/session/{sessionId}")
    @Operation(summary = "删除会话", description = "删除对话会话（逻辑删除）")
    public Result<String> deleteSession(@PathVariable String sessionId) {
        try {
            chatService.deleteSession(sessionId);
            return Result.success("删除会话成功");
        } catch (Exception e) {
            log.error("删除会话失败", e);
            return Result.fail(e.getMessage());
        }
    }

}