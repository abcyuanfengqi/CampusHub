package com.campushub.modules.lostfound.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 失物招领实体（M7）
 * 状态机: PENDING → CLAIMED → RESOLVED；PENDING → RESOLVED（未找到结案）
 * type: LOST(失物) / FOUND(招领)
 */
@Data
@TableName("lost_found")
public class LostFound {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;
    private String title;
    private String description;
    private String location;
    private LocalDate lostDate;
    private String images;
    private Long publisherId;
    private Long finderId;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
