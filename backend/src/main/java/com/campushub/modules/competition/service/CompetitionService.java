package com.campushub.modules.competition.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.modules.competition.entity.Competition;
import com.campushub.modules.competition.entity.CompetitionTeam;
import com.campushub.modules.competition.entity.TeamMember;

import java.util.List;
import java.util.Map;

public interface CompetitionService {
    Competition createCompetition(Competition c);
    Page<Competition> pageCompetitions(int page, int size, String keyword, String level);
    Competition getCompetition(Long id);
    CompetitionTeam createTeam(Long competitionId, String name, String description, int maxMembers);
    List<CompetitionTeam> teams(Long competitionId);
    void joinTeam(Long teamId);
    void leaveTeam(Long teamId);
    List<TeamMember> teamMembers(Long teamId);
    List<Map<String, Object>> myTeams(Long userId);
    void updateStatus();  // 定时更新竞赛状态（按日期）
}
