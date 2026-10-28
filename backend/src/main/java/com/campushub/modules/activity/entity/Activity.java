package com.campushub.modules.activity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动实体（M4 活动组织）
 * 状态机: DRAFT → PUBLISHED → REGISTERING → IN_PROGRESS → FINISHED；任意态可 CANCELLED
 */
@Data
@TableName("activity")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Long clubId;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String coverImage;
    private String status;
    private Long organizerId;
    private LocalDateTime createTime;
}
