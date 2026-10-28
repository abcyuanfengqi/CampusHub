package com.campushub.modules.notify.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站内消息实体（M5 信息推送）
 * type: ACTIVITY_REMIND / CLUB_APPROVAL / SYSTEM / TRADE
 */
@Data
@TableName("notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type;
    private Long refId;
    private Integer isRead;
    private LocalDateTime createTime;
}
