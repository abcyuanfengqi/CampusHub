package com.campushub.modules.competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 竞赛实体（M3 竞赛组队）
 * 状态机: RECRUITING → IN_PROGRESS → FINISHED
 */
@Data
@TableName("competition")
public class Competition {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String organizer;
    private String level;   // SCHOOL / PROVINCE / NATIONAL
    private LocalDate registerDeadline;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime createTime;
}
