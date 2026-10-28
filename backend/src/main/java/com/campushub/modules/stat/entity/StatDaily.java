package com.campushub.modules.stat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * 每日统计实体（M8 数据看板）
 * 唯一键 uk_date：每日一行
 */
@Data
@TableName("stat_daily")
public class StatDaily {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate statDate;
    private Integer newUsers;
    private Integer activeUsers;
    private Integer newActivities;
    private Integer newSecondhand;
    private Integer newClubs;
}
