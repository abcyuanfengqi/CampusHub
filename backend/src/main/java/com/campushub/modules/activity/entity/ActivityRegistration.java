package com.campushub.modules.activity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动报名实体（唯一键 uk_activity_user 一人一报）
 * 状态: REGISTERED / CANCELLED / CHECKED_IN
 */
@Data
@TableName("activity_registration")
public class ActivityRegistration {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long userId;
    private String status;
    private LocalDateTime registerTime;
    private LocalDateTime checkInTime;
}
