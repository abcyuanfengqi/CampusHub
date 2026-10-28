package com.campushub.modules.activity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动签到实体（唯一键 uk_activity_user 一人一次签到）
 */
@Data
@TableName("activity_checkin")
public class ActivityCheckin {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long userId;
    private LocalDateTime checkinTime;
    private String checkinMethod; // QR_CODE / MANUAL
}
