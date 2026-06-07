package com.speakingpractice.partner.vocabulary.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.speakingpractice.partner.vocabulary.domain.dto.*;

import java.util.List;

/**
 * 词汇服务接口
 */
public interface VocabularyService {

    /**
     * 添加词汇
     *
     * @param request 添加词汇请求
     * @return 词汇DTO
     */
    VocabularyDTO addVocabulary(AddVocabularyRequest request);

    /**
     * 更新词汇
     *
     * @param vocabularyId 词汇ID
     * @param request 更新词汇请求
     * @return 词汇DTO
     */
    VocabularyDTO updateVocabulary(String vocabularyId, UpdateVocabularyRequest request);

    /**
     * 删除词汇
     *
     * @param vocabularyId 词汇ID
     */
    void deleteVocabulary(String vocabularyId);

    /**
     * 查询词汇列表
     *
     * @param request 查询词汇请求
     * @return 分页结果
     */
    IPage<VocabularyDTO> queryVocabularies(VocabularyQueryRequest request);

    /**
     * 查询词汇详情
     *
     * @param vocabularyId 词汇ID
     * @return 词汇DTO
     */
    VocabularyDTO getVocabularyDetail(String vocabularyId);

    /**
     * 学习词汇
     *
     * @param request 学习词汇请求
     */
    void learnVocabulary(LearnVocabularyRequest request);

    /**
     * 获取词汇统计
     *
     * @return 词汇统计DTO
     */
    VocabularyStatisticsDTO getStatistics();

    /**
     * 获取需要复习的词汇列表
     *
     * @return 词汇DTO列表
     */
    List<VocabularyDTO> getNeedReviewVocabularies();

}