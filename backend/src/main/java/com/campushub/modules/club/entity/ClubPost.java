package com.campushub.modules.club.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社团动态实体（M2 信息分享）
 */
@Data
@TableName("club_post")
public class ClubPost {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long clubId;
    private Long userId;
    private String title;
    private String content;
    private String images;
    private LocalDateTime createTime;
}
