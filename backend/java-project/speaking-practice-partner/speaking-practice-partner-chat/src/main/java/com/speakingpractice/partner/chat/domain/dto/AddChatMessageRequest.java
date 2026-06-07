package com.speakingpractice.partner.chat.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 添加对话消息请求
 */
@Data
public class AddChatMessageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 角色 (USER/ASSISTANT/SYSTEM)
     */
    @NotBlank(message = "角色不能为空")
    private String role;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 音频路径
     */
    private String audioPath;

    /**
     * ASR识别置信度
     */
    private Double asrConfidence;

    /**
     * 发音评分
     */
    private Integer pronunciationScore;

    /**
     * 语法错误数量
     */
    private Integer grammarErrorCount;

    /**
     * 是否已纠错 (0-否 1-是)
     */
    private Integer corrected;

}