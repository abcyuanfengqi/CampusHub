package com.campushub.modules.club.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社团成员实体（唯一键 uk_club_user 防重复入社）
 */
@Data
@TableName("club_member")
public class ClubMember {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long clubId;
    private Long userId;
    private String role;   // LEADER / MEMBER
    private String status; // PENDING / APPROVED / REJECTED
    private LocalDateTime joinTime;
}
