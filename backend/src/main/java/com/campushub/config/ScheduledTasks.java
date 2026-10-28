package com.campushub.config;

import com.campushub.modules.activity.service.ActivityService;
import com.campushub.modules.competition.service.CompetitionService;
import com.campushub.modules.stat.service.impl.StatServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 定时任务（R1 运行视图：定时任务线程）
 * 1. 每日00:05 生成前一天统计（uk_date 幂等）
 * 2. 每分钟检查一次活动/竞赛状态自动流转（状态机按时间推进）
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTasks {

    private final StatServiceImpl statService;
    private final ActivityService activityService;
    private final CompetitionService competitionService;

    @Scheduled(cron = "0 5 0 * * ?")
    public void dailyStat() {
        log.info("定时任务：生成每日统计 {}", LocalDate.now().minusDays(1));
        try {
            statService.generateDailyStat(LocalDate.now().minusDays(1));
        } catch (Exception e) {
            log.error("每日统计生成失败", e);
        }
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void autoStatus() {
        try {
            activityService.updateStatusByTime();
            competitionService.updateStatus();
        } catch (Exception e) {
            log.error("状态自动流转失败", e);
        }
    }
}
