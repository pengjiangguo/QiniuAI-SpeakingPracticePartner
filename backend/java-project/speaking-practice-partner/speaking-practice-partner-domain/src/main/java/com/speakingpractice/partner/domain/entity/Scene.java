package com.speakingpractice.partner.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 场景实体
 */
@Data
@TableName("scenes")
public class Scene implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 场景名称（中文）
     */
    private String name;

    /**
     * 场景名称（英文）
     */
    private String nameEn;

    /**
     * 场景描述
     */
    private String description;

    /**
     * 难度等级 (BEGINNER/INTERMEDIATE/ADVANCED)
     */
    private String difficulty;

    /**
     * 角色描述
     */
    private String roleDescription;

    /**
     * 场景上下文
     */
    private String sceneContext;

    /**
     * 开场白
     */
    private String openingLine;

    /**
     * 建议话题（JSON数组）
     */
    private String suggestedTopics;

    /**
     * 核心词汇（JSON数组）
     */
    private String coreVocabulary;

    /**
     * 标签（JSON数组）
     */
    private String tags;

    /**
     * 封面图
     */
    private String coverImage;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态 (0-禁用 1-正常)
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 是否删除 (0-否 1-是)
     */
    @TableLogic
    private Integer deleted;

}
