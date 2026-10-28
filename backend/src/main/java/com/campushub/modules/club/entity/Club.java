package com.campushub.modules.club.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社团实体（M2 社团管理）
 * 状态机: PENDING → APPROVED / REJECTED → DISBANDED
 */
@Data
@TableName("club")
public class Club {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String category;
    private String logo;
    private Long leaderId;
    private Integer memberCount;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
