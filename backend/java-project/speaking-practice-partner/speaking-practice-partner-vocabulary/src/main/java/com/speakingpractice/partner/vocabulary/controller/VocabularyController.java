package com.speakingpractice.partner.vocabulary.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.speakingpractice.partner.common.result.Result;
import com.speakingpractice.partner.vocabulary.domain.dto.*;
import com.speakingpractice.partner.vocabulary.service.VocabularyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 词汇控制器
 */
@Slf4j
@RestController
@RequestMapping("/vocabulary")
@Tag(name = "词汇管理", description = "词汇学习相关接口")
public class VocabularyController {

    @Autowired
    private VocabularyService vocabularyService;

    /**
     * 添加词汇
     */
    @PostMapping("/add")
    @Operation(summary = "添加词汇", description = "将新单词添加到生词本")
    public Result<VocabularyDTO> addVocabulary(@Validated @RequestBody AddVocabularyRequest request) {
        try {
            VocabularyDTO vocabulary = vocabularyService.addVocabulary(request);
            return Result.success("添加词汇成功", vocabulary);
        } catch (Exception e) {
            log.error("添加词汇失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 更新词汇
     */
    @PutMapping("/{vocabularyId}")
    @Operation(summary = "更新词汇", description = "更新词汇的释义、例句等信息")
    public Result<VocabularyDTO> updateVocabulary(
            @PathVariable String vocabularyId,
            @Validated @RequestBody UpdateVocabularyRequest request) {
        try {
            VocabularyDTO vocabulary = vocabularyService.updateVocabulary(vocabularyId, request);
            return Result.success("更新词汇成功", vocabulary);
        } catch (Exception e) {
            log.error("更新词汇失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 删除词汇
     */
    @DeleteMapping("/{vocabularyId}")
    @Operation(summary = "删除词汇", description = "从生词本中删除词汇")
    public Result<String> deleteVocabulary(@PathVariable String vocabularyId) {
        try {
            vocabularyService.deleteVocabulary(vocabularyId);
            return Result.success("删除词汇成功");
        } catch (Exception e) {
            log.error("删除词汇失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 查询词汇列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询词汇列表", description = "分页查询生词本，支持关键词搜索和筛选")
    public Result<IPage<VocabularyDTO>> queryVocabularies(VocabularyQueryRequest request) {
        try {
            IPage<VocabularyDTO> page = vocabularyService.queryVocabularies(request);
            return Result.success(page);
        } catch (Exception e) {
            log.error("查询词汇列表失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 查询词汇详情
     */
    @GetMapping("/{vocabularyId}")
    @Operation(summary = "查询词汇详情", description = "获取词汇的详细信息")
    public Result<VocabularyDTO> getVocabularyDetail(@PathVariable String vocabularyId) {
        try {
            VocabularyDTO vocabulary = vocabularyService.getVocabularyDetail(vocabularyId);
            return Result.success(vocabulary);
        } catch (Exception e) {
            log.error("查询词汇详情失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 学习词汇
     */
    @PostMapping("/learn")
    @Operation(summary = "学习词汇", description = "记录词汇学习过程，更新掌握程度")
    public Result<String> learnVocabulary(@Validated @RequestBody LearnVocabularyRequest request) {
        try {
            vocabularyService.learnVocabulary(request);
            return Result.success("学习记录已保存");
        } catch (Exception e) {
            log.error("学习词汇失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取词汇统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取词汇统计", description = "获取词汇学习的统计数据")
    public Result<VocabularyStatisticsDTO> getStatistics() {
        try {
            VocabularyStatisticsDTO statistics = vocabularyService.getStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取词汇统计失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取需要复习的词汇列表
     */
    @GetMapping("/review")
    @Operation(summary = "获取需要复习的词汇列表", description = "获取当前需要复习的词汇")
    public Result<List<VocabularyDTO>> getNeedReviewVocabularies() {
        try {
            List<VocabularyDTO> vocabularies = vocabularyService.getNeedReviewVocabularies();
            return Result.success(vocabularies);
        } catch (Exception e) {
            log.error("获取需要复习的词汇列表失败", e);
            return Result.fail(e.getMessage());
        }
    }

}