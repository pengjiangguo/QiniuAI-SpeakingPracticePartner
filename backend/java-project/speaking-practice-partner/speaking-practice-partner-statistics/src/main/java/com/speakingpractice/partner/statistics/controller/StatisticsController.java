package com.speakingpractice.partner.statistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.speakingpractice.partner.common.result.Result;
import com.speakingpractice.partner.statistics.domain.dto.*;
import com.speakingpractice.partner.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/statistics")
@Tag(name = "学习统计", description = "学习数据统计相关接口")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取学习统计概览
     */
    @GetMapping("/overview")
    @Operation(summary = "获取学习统计概览", description = "获取用户的总体学习统计数据")
    public Result<LearningStatisticsOverviewDTO> getOverview(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            LearningStatisticsOverviewDTO overview = statisticsService.getOverview(startDate, endDate);
            return Result.success(overview);
        } catch (Exception e) {
            log.error("获取学习统计概览失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取练习趋势
     */
    @GetMapping("/trend")
    @Operation(summary = "获取练习趋势", description = "获取用户的练习趋势数据（按日期统计）")
    public Result<List<PracticeTrendDTO>> getPracticeTrend(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            List<PracticeTrendDTO> trends = statisticsService.getPracticeTrend(startDate, endDate);
            return Result.success(trends);
        } catch (Exception e) {
            log.error("获取练习趋势失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取场景分布
     */
    @GetMapping("/scene-distribution")
    @Operation(summary = "获取场景分布", description = "获取用户在不同场景的练习分布数据")
    public Result<List<SceneDistributionDTO>> getSceneDistribution(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            List<SceneDistributionDTO> distributions = statisticsService.getSceneDistribution(startDate, endDate);
            return Result.success(distributions);
        } catch (Exception e) {
            log.error("获取场景分布失败", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取最近练习记录
     */
    @GetMapping("/recent-records")
    @Operation(summary = "获取最近练习记录", description = "获取用户的最近练习记录（分页）")
    public Result<IPage<RecentPracticeRecordDTO>> getRecentRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            IPage<RecentPracticeRecordDTO> records = statisticsService.getRecentRecords(pageNum, pageSize);
            return Result.success(records);
        } catch (Exception e) {
            log.error("获取最近练习记录失败", e);
            return Result.fail(e.getMessage());
        }
    }

}
