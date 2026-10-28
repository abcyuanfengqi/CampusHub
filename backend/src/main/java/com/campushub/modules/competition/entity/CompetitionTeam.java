package com.campushub.modules.competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 竞赛队伍实体（唯一键 uk_team_user 防重复入队）
 * 状态机: RECRUITING → FULL → REGISTERED → FINISHED
 */
@Data
@TableName("competition_team")
public class CompetitionTeam {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long competitionId;
    private String name;
    private Long leaderId;
    private String description;
    private Integer memberCount;
    private Integer maxMembers;
    private String status;
    private LocalDateTime createTime;
}
