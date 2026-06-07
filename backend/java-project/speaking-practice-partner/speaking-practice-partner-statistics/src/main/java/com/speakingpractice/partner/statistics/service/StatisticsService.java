package com.speakingpractice.partner.statistics.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.speakingpractice.partner.chat.domain.entity.ChatSession;
import com.speakingpractice.partner.chat.mapper.ChatSessionMapper;
import com.speakingpractice.partner.statistics.domain.dto.*;
import com.speakingpractice.partner.vocabulary.domain.entity.Vocabulary;
import com.speakingpractice.partner.vocabulary.mapper.VocabularyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.dev33.satoken.stp.StpUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计服务
 */
@Slf4j
@Service
public class StatisticsService {

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    /**
     * 获取学习统计概览
     */
    public LearningStatisticsOverviewDTO getOverview(LocalDate startDate, LocalDate endDate) {
        String userId = StpUtil.getLoginIdAsString();

        // 转换日期为 LocalDateTime
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        // 查询对话会话
        LambdaQueryWrapper<ChatSession> sessionWrapper = new LambdaQueryWrapper<>();
        sessionWrapper.eq(ChatSession::getUserId, userId)
                .eq(ChatSession::getDeleted, 0)
                .eq(ChatSession::getStatus, "ENDED");

        if (startDateTime != null && endDateTime != null) {
            sessionWrapper.between(ChatSession::getStartTime, startDateTime, endDateTime);
        }

        List<ChatSession> sessions = chatSessionMapper.selectList(sessionWrapper);

        // 查询词汇
        LambdaQueryWrapper<Vocabulary> vocabularyWrapper = new LambdaQueryWrapper<>();
        vocabularyWrapper.eq(Vocabulary::getUserId, userId)
                .eq(Vocabulary::getDeleted, 0);

        if (startDateTime != null && endDateTime != null) {
            vocabularyWrapper.between(Vocabulary::getCreatedAt, startDateTime, endDateTime);
        }

        List<Vocabulary> vocabularies = vocabularyMapper.selectList(vocabularyWrapper);

        // 计算统计数据
        LearningStatisticsOverviewDTO overview = new LearningStatisticsOverviewDTO();

        // 总对话次数
        overview.setTotalDialogues(sessions.size());

        // 总练习时长（分钟，向上取整）
        int totalSeconds = sessions.stream()
                .filter(s -> s.getDurationSeconds() != null)
                .mapToInt(ChatSession::getDurationSeconds)
                .sum();
        overview.setTotalMinutes((int) Math.ceil(totalSeconds / 60.0));

        // 平均评分
        double avgScore = sessions.stream()
                .filter(s -> s.getOverallScore() != null)
                .mapToInt(ChatSession::getOverallScore)
                .average()
                .orElse(0.0);
        overview.setAverageScore(Math.round(avgScore * 10) / 10.0);

        // 新增词汇数
        overview.setNewWords(vocabularies.size());

        // 已掌握词汇数
        int masteredCount = Math.toIntExact(vocabularies.stream()
                .filter(v -> v.getMasteryLevel() != null && v.getMasteryLevel() == 2)
                .count());
        overview.setMasteredWords(masteredCount);

        // 学习中词汇数
        int learningCount = Math.toIntExact(vocabularies.stream()
                .filter(v -> v.getMasteryLevel() != null && v.getMasteryLevel() == 1)
                .count());
        overview.setLearningWords(learningCount);

        // 需复习词汇数
        int needReviewCount = Math.toIntExact(vocabularies.stream()
                .filter(v -> v.getMasteryLevel() != null && v.getMasteryLevel() == 3)
                .count());
        overview.setNeedReviewWords(needReviewCount);

        // 本周对话次数
        LocalDate weekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        LocalDateTime weekStartDateTime = weekStart.atStartOfDay();
        int weekDialogues = (int) sessions.stream()
                .filter(s -> s.getStartTime() != null && s.getStartTime().isAfter(weekStartDateTime))
                .count();
        overview.setWeekDialogues(weekDialogues);

        // 本月对话次数
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDateTime monthStartDateTime = monthStart.atStartOfDay();
        int monthDialogues = (int) sessions.stream()
                .filter(s -> s.getStartTime() != null && s.getStartTime().isAfter(monthStartDateTime))
                .count();
        overview.setMonthDialogues(monthDialogues);

        // 连续学习天数（简化计算，实际需要更复杂的逻辑）
        overview.setConsecutiveDays(calculateConsecutiveDays(sessions));

        return overview;
    }

    /**
     * 获取练习趋势
     */
    public List<PracticeTrendDTO> getPracticeTrend(LocalDate startDate, LocalDate endDate) {
        String userId = StpUtil.getLoginIdAsString();

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);

        // 查询对话会话
        LambdaQueryWrapper<ChatSession> sessionWrapper = new LambdaQueryWrapper<>();
        sessionWrapper.eq(ChatSession::getUserId, userId)
                .eq(ChatSession::getDeleted, 0)
                .eq(ChatSession::getStatus, "ENDED")
                .between(ChatSession::getStartTime, startDateTime, endDateTime);

        List<ChatSession> sessions = chatSessionMapper.selectList(sessionWrapper);

        // 查询词汇
        LambdaQueryWrapper<Vocabulary> vocabularyWrapper = new LambdaQueryWrapper<>();
        vocabularyWrapper.eq(Vocabulary::getUserId, userId)
                .eq(Vocabulary::getDeleted, 0)
                .between(Vocabulary::getCreatedAt, startDateTime, endDateTime);

        List<Vocabulary> vocabularies = vocabularyMapper.selectList(vocabularyWrapper);

        // 按日期分组统计
        Map<LocalDate, List<ChatSession>> sessionGroupByDate = sessions.stream()
                .filter(s -> s.getStartTime() != null)
                .collect(Collectors.groupingBy(s -> s.getStartTime().toLocalDate()));

        Map<LocalDate, List<Vocabulary>> vocabularyGroupByDate = vocabularies.stream()
                .filter(v -> v.getCreatedAt() != null)
                .collect(Collectors.groupingBy(v -> v.getCreatedAt().toLocalDate()));

        // 生成趋势数据
        List<PracticeTrendDTO> trends = new ArrayList<>();
        LocalDate current = startDateTime.toLocalDate();
        while (!current.isAfter(endDateTime.toLocalDate())) {
            PracticeTrendDTO trend = new PracticeTrendDTO();
            trend.setDate(current);

            // 对话次数
            List<ChatSession> daySessions = sessionGroupByDate.getOrDefault(current, new ArrayList<>());
            trend.setDialogueCount(daySessions.size());

            // 练习时长（分钟，向上取整）
            int dayDuration = daySessions.stream()
                    .filter(s -> s.getDurationSeconds() != null)
                    .mapToInt(ChatSession::getDurationSeconds)
                    .sum();
            trend.setDurationMinutes((int) Math.ceil(dayDuration / 60.0));

            // 平均评分
            double dayAvgScore = daySessions.stream()
                    .filter(s -> s.getOverallScore() != null)
                    .mapToInt(ChatSession::getOverallScore)
                    .average()
                    .orElse(0.0);
            trend.setAverageScore(Math.round(dayAvgScore * 10) / 10.0);

            // 新增词汇数
            List<Vocabulary> dayVocabularies = vocabularyGroupByDate.getOrDefault(current, new ArrayList<>());
            trend.setNewWordsCount(dayVocabularies.size());

            trends.add(trend);
            current = current.plusDays(1);
        }

        return trends;
    }

    /**
     * 获取场景分布
     */
    public List<SceneDistributionDTO> getSceneDistribution(LocalDate startDate, LocalDate endDate) {
        String userId = StpUtil.getLoginIdAsString();

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        // 查询对话会话
        LambdaQueryWrapper<ChatSession> sessionWrapper = new LambdaQueryWrapper<>();
        sessionWrapper.eq(ChatSession::getUserId, userId)
                .eq(ChatSession::getDeleted, 0)
                .eq(ChatSession::getStatus, "ENDED");

        if (startDateTime != null && endDateTime != null) {
            sessionWrapper.between(ChatSession::getStartTime, startDateTime, endDateTime);
        }

        List<ChatSession> sessions = chatSessionMapper.selectList(sessionWrapper);

        // 按场景分组统计
        Map<String, List<ChatSession>> sessionGroupByScene = sessions.stream()
                .filter(s -> s.getSceneId() != null)
                .collect(Collectors.groupingBy(ChatSession::getSceneId));

        // 计算总数
        int totalDialogues = sessions.size();

        // 生成场景分布数据
        List<SceneDistributionDTO> distributions = new ArrayList<>();
        for (Map.Entry<String, List<ChatSession>> entry : sessionGroupByScene.entrySet()) {
            SceneDistributionDTO distribution = new SceneDistributionDTO();
            distribution.setSceneId(entry.getKey());
            distribution.setSceneName(getSceneName(entry.getKey()));
            distribution.setDialogueCount(entry.getValue().size());

            // 占比
            double percentage = totalDialogues > 0 ? (entry.getValue().size() * 100.0 / totalDialogues) : 0.0;
            distribution.setPercentage(Math.round(percentage * 10) / 10.0);

            // 平均评分
            double avgScore = entry.getValue().stream()
                    .filter(s -> s.getOverallScore() != null)
                    .mapToInt(ChatSession::getOverallScore)
                    .average()
                    .orElse(0.0);
            distribution.setAverageScore(Math.round(avgScore * 10) / 10.0);

            // 总练习时长（分钟，向上取整）
            int totalDuration = entry.getValue().stream()
                    .filter(s -> s.getDurationSeconds() != null)
                    .mapToInt(ChatSession::getDurationSeconds)
                    .sum();
            distribution.setTotalDuration((int) Math.ceil(totalDuration / 60.0));

            distributions.add(distribution);
        }

        return distributions;
    }

    /**
     * 获取最近练习记录
     */
    public IPage<RecentPracticeRecordDTO> getRecentRecords(Integer pageNum, Integer pageSize) {
        String userId = StpUtil.getLoginIdAsString();

        // 查询对话会话（分页）
        Page<ChatSession> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ChatSession> sessionWrapper = new LambdaQueryWrapper<>();
        sessionWrapper.eq(ChatSession::getUserId, userId)
                .eq(ChatSession::getDeleted, 0)
                .eq(ChatSession::getStatus, "ENDED")
                .orderByDesc(ChatSession::getStartTime);

        IPage<ChatSession> sessionPage = chatSessionMapper.selectPage(page, sessionWrapper);

        // 转换为DTO
        IPage<RecentPracticeRecordDTO> dtoPage = new Page<>(pageNum, pageSize, sessionPage.getTotal());
        List<RecentPracticeRecordDTO> records = sessionPage.getRecords().stream()
                .map(this::convertToRecentRecordDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(records);

        return dtoPage;
    }

    /**
     * 转换为最近练习记录DTO
     */
    private RecentPracticeRecordDTO convertToRecentRecordDTO(ChatSession session) {
        RecentPracticeRecordDTO record = new RecentPracticeRecordDTO();
        record.setSessionId(session.getId());
        record.setDate(session.getStartTime());
        record.setSceneId(session.getSceneId());
        record.setSceneName(getSceneName(session.getSceneId()));
        // 时长（分钟，向上取整）
        record.setDurationMinutes(session.getDurationSeconds() != null ? 
            (int) Math.ceil(session.getDurationSeconds() / 60.0) : 0);
        record.setScore(session.getOverallScore() != null ? session.getOverallScore() : 0);
        record.setMessageCount(session.getMessageCount() != null ? session.getMessageCount() : 0);
        record.setWordsCount(0); // 需要额外查询词汇数，暂时设为0
        return record;
    }

    /**
     * 获取场景名称
     */
    private String getSceneName(String sceneId) {
        if (sceneId == null) return "未分类";

        // 场景名称映射
        Map<String, String> sceneNames = Map.of(
                "daily", "日常对话",
                "restaurant", "餐厅点餐",
                "shopping", "购物",
                "travel", "旅行",
                "interview", "面试",
                "meeting", "会议"
        );

        return sceneNames.getOrDefault(sceneId, sceneId);
    }

    /**
     * 计算连续学习天数
     */
    private Integer calculateConsecutiveDays(List<ChatSession> sessions) {
        if (sessions.isEmpty()) return 0;

        // 按日期分组
        Map<LocalDate, List<ChatSession>> sessionGroupByDate = sessions.stream()
                .filter(s -> s.getStartTime() != null)
                .collect(Collectors.groupingBy(s -> s.getStartTime().toLocalDate()));

        // 从今天开始往前计算连续天数
        LocalDate today = LocalDate.now();
        int consecutiveDays = 0;
        LocalDate current = today;

        while (sessionGroupByDate.containsKey(current)) {
            consecutiveDays++;
            current = current.minusDays(1);
        }

        return consecutiveDays;
    }

}
