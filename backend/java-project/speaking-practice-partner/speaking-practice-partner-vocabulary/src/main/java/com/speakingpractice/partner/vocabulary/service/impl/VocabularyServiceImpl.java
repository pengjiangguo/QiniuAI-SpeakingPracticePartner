package com.speakingpractice.partner.vocabulary.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.speakingpractice.partner.common.exception.BusinessException;
import com.speakingpractice.partner.vocabulary.domain.dto.*;
import com.speakingpractice.partner.vocabulary.domain.entity.Vocabulary;
import com.speakingpractice.partner.vocabulary.domain.entity.VocabularyLearningRecord;
import com.speakingpractice.partner.vocabulary.mapper.VocabularyMapper;
import com.speakingpractice.partner.vocabulary.mapper.VocabularyLearningRecordMapper;
import com.speakingpractice.partner.vocabulary.service.VocabularyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 词汇服务实现类
 */
@Slf4j
@Service
public class VocabularyServiceImpl implements VocabularyService {

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private VocabularyLearningRecordMapper learningRecordMapper;

    /**
     * 添加词汇
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public VocabularyDTO addVocabulary(AddVocabularyRequest request) {
        String userId = StpUtil.getLoginIdAsString();

        // 检查是否已存在该单词
        LambdaQueryWrapper<Vocabulary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vocabulary::getUserId, userId)
                .eq(Vocabulary::getWord, request.getWord())
                .eq(Vocabulary::getDeleted, 0);

        Vocabulary existVocabulary = vocabularyMapper.selectOne(wrapper);
        if (existVocabulary != null) {
            throw new BusinessException("该单词已存在于生词本中");
        }

        // 创建词汇实体
        Vocabulary vocabulary = new Vocabulary();
        BeanUtils.copyProperties(request, vocabulary);
        vocabulary.setUserId(userId);
        vocabulary.setMasteryLevel(0); // 未学习
        vocabulary.setLearnCount(0);
        vocabulary.setReviewCount(0);
        vocabulary.setCorrectCount(0);
        vocabulary.setWrongCount(0);
        vocabulary.setStatus(1);

        // 插入数据库
        vocabularyMapper.insert(vocabulary);

        // 返回DTO
        return convertToDTO(vocabulary);
    }

    /**
     * 更新词汇
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public VocabularyDTO updateVocabulary(String vocabularyId, UpdateVocabularyRequest request) {
        String userId = StpUtil.getLoginIdAsString();

        // 查询词汇
        Vocabulary vocabulary = vocabularyMapper.selectById(vocabularyId);
        if (vocabulary == null || vocabulary.getDeleted() == 1) {
            throw new BusinessException("词汇不存在");
        }

        // 检查是否属于当前用户
        if (!vocabulary.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改该词汇");
        }

        // 更新词汇信息
        BeanUtils.copyProperties(request, vocabulary);
        vocabularyMapper.updateById(vocabulary);

        return convertToDTO(vocabulary);
    }

    /**
     * 删除词汇
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVocabulary(String vocabularyId) {
        String userId = StpUtil.getLoginIdAsString();

        // 查询词汇
        Vocabulary vocabulary = vocabularyMapper.selectById(vocabularyId);
        if (vocabulary == null || vocabulary.getDeleted() == 1) {
            throw new BusinessException("词汇不存在");
        }

        // 检查是否属于当前用户
        if (!vocabulary.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除该词汇");
        }

        // 逻辑删除（使用 MyBatis-Plus 的 deleteById，会自动将 deleted 字段设置为 1）
        vocabularyMapper.deleteById(vocabularyId);
    }

    /**
     * 查询词汇列表
     */
    @Override
    public IPage<VocabularyDTO> queryVocabularies(VocabularyQueryRequest request) {
        String userId = StpUtil.getLoginIdAsString();

        // 构建查询条件
        LambdaQueryWrapper<Vocabulary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vocabulary::getUserId, userId)
                .eq(Vocabulary::getDeleted, 0);

        // 关键词搜索
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(Vocabulary::getWord, request.getKeyword())
                    .or()
                    .like(Vocabulary::getMeaningCn, request.getKeyword())
                    .or()
                    .like(Vocabulary::getMeaningEn, request.getKeyword()));
        }

        // 掌握程度筛选
        if (request.getMasteryLevel() != null) {
            wrapper.eq(Vocabulary::getMasteryLevel, request.getMasteryLevel());
        }

        // 难度等级筛选
        if (request.getDifficulty() != null && !request.getDifficulty().isEmpty()) {
            wrapper.eq(Vocabulary::getDifficulty, request.getDifficulty());
        }

        // 来源场景筛选
        if (request.getSceneId() != null && !request.getSceneId().isEmpty()) {
            wrapper.eq(Vocabulary::getSceneId, request.getSceneId());
        }

        // 需要复习筛选
        if (request.getNeedReview() != null && request.getNeedReview()) {
            wrapper.eq(Vocabulary::getMasteryLevel, 3) // 需复习
                    .le(Vocabulary::getNextReviewAt, LocalDateTime.now());
        }

        // 排序：按创建时间倒序
        wrapper.orderByDesc(Vocabulary::getCreatedAt);

        // 分页查询
        Page<Vocabulary> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<Vocabulary> vocabularyPage = vocabularyMapper.selectPage(page, wrapper);

        // 转换为DTO
        IPage<VocabularyDTO> dtoPage = new Page<>(vocabularyPage.getCurrent(), vocabularyPage.getSize(), vocabularyPage.getTotal());
        List<VocabularyDTO> dtoList = vocabularyPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /**
     * 查询词汇详情
     */
    @Override
    public VocabularyDTO getVocabularyDetail(String vocabularyId) {
        String userId = StpUtil.getLoginIdAsString();

        // 查询词汇
        Vocabulary vocabulary = vocabularyMapper.selectById(vocabularyId);
        if (vocabulary == null || vocabulary.getDeleted() == 1) {
            throw new BusinessException("词汇不存在");
        }

        // 检查是否属于当前用户
        if (!vocabulary.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看该词汇");
        }

        return convertToDTO(vocabulary);
    }

    /**
     * 学习词汇
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void learnVocabulary(LearnVocabularyRequest request) {
        String userId = StpUtil.getLoginIdAsString();

        // 查询词汇
        Vocabulary vocabulary = vocabularyMapper.selectById(request.getVocabularyId());
        if (vocabulary == null || vocabulary.getDeleted() == 1) {
            throw new BusinessException("词汇不存在");
        }

        // 检查是否属于当前用户
        if (!vocabulary.getUserId().equals(userId)) {
            throw new BusinessException("无权限学习该词汇");
        }

        // 创建学习记录
        VocabularyLearningRecord record = new VocabularyLearningRecord();
        BeanUtils.copyProperties(request, record);
        record.setUserId(userId);
        record.setVocabularyId(request.getVocabularyId());
        record.setLearnAt(LocalDateTime.now());
        learningRecordMapper.insert(record);

        // 更新词汇统计
        updateVocabularyStats(vocabulary, request);

        // 更新掌握程度
        updateMasteryLevel(vocabulary, request);
    }

    /**
     * 获取词汇统计
     */
    @Override
    public VocabularyStatisticsDTO getStatistics() {
        String userId = StpUtil.getLoginIdAsString();

        // 统计各状态词汇数量
        LambdaQueryWrapper<Vocabulary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vocabulary::getUserId, userId)
                .eq(Vocabulary::getDeleted, 0);

        List<Vocabulary> vocabularies = vocabularyMapper.selectList(wrapper);

        // 计算统计数据
        VocabularyStatisticsDTO stats = new VocabularyStatisticsDTO();
        stats.setTotalVocabularies(vocabularies.size());
        stats.setUnlearnedCount((int) vocabularies.stream().filter(v -> v.getMasteryLevel() == 0).count());
        stats.setLearningCount((int) vocabularies.stream().filter(v -> v.getMasteryLevel() == 1).count());
        stats.setMasteredCount((int) vocabularies.stream().filter(v -> v.getMasteryLevel() == 2).count());
        stats.setNeedReviewCount((int) vocabularies.stream().filter(v -> v.getMasteryLevel() == 3).count());

        // 今日学习统计
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        LambdaQueryWrapper<VocabularyLearningRecord> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(VocabularyLearningRecord::getUserId, userId)
                .between(VocabularyLearningRecord::getLearnAt, todayStart, todayEnd)
                .eq(VocabularyLearningRecord::getDeleted, 0);

        List<VocabularyLearningRecord> todayRecords = learningRecordMapper.selectList(todayWrapper);
        stats.setTodayLearnedCount((int) todayRecords.stream().filter(r -> "LEARN".equals(r.getLearnType())).count());
        stats.setTodayReviewedCount((int) todayRecords.stream().filter(r -> "REVIEW".equals(r.getLearnType())).count());

        // 总学习次数和复习次数
        stats.setTotalLearnCount((int) vocabularies.stream().mapToInt(Vocabulary::getLearnCount).sum());
        stats.setTotalReviewCount((int) vocabularies.stream().mapToInt(Vocabulary::getReviewCount).sum());

        // 平均正确率
        int totalCorrect = vocabularies.stream().mapToInt(Vocabulary::getCorrectCount).sum();
        int totalWrong = vocabularies.stream().mapToInt(Vocabulary::getWrongCount).sum();
        int totalAttempts = totalCorrect + totalWrong;
        if (totalAttempts > 0) {
            stats.setAverageCorrectRate((double) totalCorrect / totalAttempts * 100);
        } else {
            stats.setAverageCorrectRate(0.0);
        }

        // 总学习时长
        int totalDuration = todayRecords.stream().mapToInt(r -> r.getDuration() != null ? r.getDuration() : 0).sum();
        stats.setTotalDurationMinutes(totalDuration / 60);

        return stats;
    }

    /**
     * 获取需要复习的词汇列表
     */
    @Override
    public List<VocabularyDTO> getNeedReviewVocabularies() {
        String userId = StpUtil.getLoginIdAsString();

        LambdaQueryWrapper<Vocabulary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vocabulary::getUserId, userId)
                .eq(Vocabulary::getDeleted, 0)
                .eq(Vocabulary::getMasteryLevel, 3) // 需复习
                .le(Vocabulary::getNextReviewAt, LocalDateTime.now())
                .orderByAsc(Vocabulary::getNextReviewAt);

        List<Vocabulary> vocabularies = vocabularyMapper.selectList(wrapper);
        return vocabularies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 更新词汇统计
     */
    private void updateVocabularyStats(Vocabulary vocabulary, LearnVocabularyRequest request) {
        // 更新学习次数或复习次数
        if ("LEARN".equals(request.getLearnType())) {
            vocabulary.setLearnCount(vocabulary.getLearnCount() + 1);
        } else if ("REVIEW".equals(request.getLearnType())) {
            vocabulary.setReviewCount(vocabulary.getReviewCount() + 1);
        }

        // 更新正确次数或错误次数
        if (request.getIsCorrect()) {
            vocabulary.setCorrectCount(vocabulary.getCorrectCount() + 1);
        } else {
            vocabulary.setWrongCount(vocabulary.getWrongCount() + 1);
        }

        // 更新最后学习时间
        vocabulary.setLastLearnAt(LocalDateTime.now());

        vocabularyMapper.updateById(vocabulary);
    }

    /**
     * 更新掌握程度
     */
    private void updateMasteryLevel(Vocabulary vocabulary, LearnVocabularyRequest request) {
        // 计算正确率
        int totalAttempts = vocabulary.getCorrectCount() + vocabulary.getWrongCount();
        double correctRate = totalAttempts > 0 ? (double) vocabulary.getCorrectCount() / totalAttempts : 0;

        // 根据正确率更新掌握程度
        if (correctRate >= 0.8 && vocabulary.getReviewCount() >= 3) {
            vocabulary.setMasteryLevel(2); // 已掌握
        } else if (correctRate < 0.5) {
            vocabulary.setMasteryLevel(3); // 需复习
            // 设置下次复习时间（1天后）
            vocabulary.setNextReviewAt(LocalDateTime.now().plusDays(1));
        } else if (vocabulary.getMasteryLevel() == 0) {
            vocabulary.setMasteryLevel(1); // 学习中
            // 设置下次复习时间（3天后）
            vocabulary.setNextReviewAt(LocalDateTime.now().plusDays(3));
        }

        vocabularyMapper.updateById(vocabulary);
    }

    /**
     * 转换为DTO
     */
    private VocabularyDTO convertToDTO(Vocabulary vocabulary) {
        VocabularyDTO dto = new VocabularyDTO();
        BeanUtils.copyProperties(vocabulary, dto);

        // 计算正确率
        int totalAttempts = vocabulary.getCorrectCount() + vocabulary.getWrongCount();
        if (totalAttempts > 0) {
            dto.setCorrectRate((double) vocabulary.getCorrectCount() / totalAttempts * 100);
        } else {
            dto.setCorrectRate(0.0);
        }

        return dto;
    }

}