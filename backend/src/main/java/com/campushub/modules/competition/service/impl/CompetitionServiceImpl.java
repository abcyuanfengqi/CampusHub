package com.campushub.modules.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.utils.UserContext;
import com.campushub.modules.competition.entity.Competition;
import com.campushub.modules.competition.entity.CompetitionTeam;
import com.campushub.modules.competition.entity.TeamMember;
import com.campushub.modules.competition.mapper.CompetitionMapper;
import com.campushub.modules.competition.mapper.CompetitionTeamMapper;
import com.campushub.modules.competition.mapper.TeamMemberMapper;
import com.campushub.modules.competition.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 竞赛组队服务（M3）
 * 状态机: RECRUITING → IN_PROGRESS → FINISHED
 */
@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionMapper competitionMapper;
    private final CompetitionTeamMapper teamMapper;
    private final TeamMemberMapper memberMapper;

    @Override
    public Competition createCompetition(Competition c) {
        c.setStatus("RECRUITING");
        c.setCreateTime(LocalDateTime.now());
        competitionMapper.insert(c);
        return c;
    }

    @Override
    public Page<Competition> pageCompetitions(int page, int size, String keyword, String level) {
        LambdaQueryWrapper<Competition> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like(Competition::getName, keyword);
        }
        if (StringUtils.hasText(level)) {
            qw.eq(Competition::getLevel, level);
        }
        qw.orderByDesc(Competition::getCreateTime);
        return competitionMapper.selectPage(new Page<>(page, size), qw);
    }

    @Override
    public Competition getCompetition(Long id) {
        Competition c = competitionMapper.selectById(id);
        if (c == null) {
            throw new BusinessException("竞赛不存在");
        }
        return c;
    }

    @Override
    @Transactional
    public CompetitionTeam createTeam(Long competitionId, String name, String description, int maxMembers) {
        getCompetition(competitionId);
        Long userId = UserContext.getUserId();
        Long count = teamMapper.selectCount(
                new LambdaQueryWrapper<CompetitionTeam>()
                        .eq(CompetitionTeam::getCompetitionId, competitionId)
                        .eq(CompetitionTeam::getLeaderId, userId));
        if (count > 0) {
            throw new BusinessException("您已在该竞赛创建过队伍");
        }
        CompetitionTeam team = new CompetitionTeam();
        team.setCompetitionId(competitionId);
        team.setName(name);
        team.setLeaderId(userId);
        team.setDescription(description);
        team.setMemberCount(1);
        team.setMaxMembers(maxMembers > 0 ? maxMembers : 5);
        team.setStatus("RECRUITING");
        team.setCreateTime(LocalDateTime.now());
        teamMapper.insert(team);

        TeamMember leader = new TeamMember();
        leader.setTeamId(team.getId());
        leader.setUserId(userId);
        leader.setRole("LEADER");
        leader.setJoinTime(LocalDateTime.now());
        memberMapper.insert(leader);
        return team;
    }

    @Override
    public List<CompetitionTeam> teams(Long competitionId) {
        return teamMapper.selectList(
                new LambdaQueryWrapper<CompetitionTeam>()
                        .eq(CompetitionTeam::getCompetitionId, competitionId)
                        .orderByDesc(CompetitionTeam::getCreateTime));
    }

    @Override
    @Transactional
    public void joinTeam(Long teamId) {
        CompetitionTeam team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException("队伍不存在");
        }
        if ("FULL".equals(team.getStatus()) || team.getMemberCount() >= team.getMaxMembers()) {
            throw new BusinessException("队伍已满员");
        }
        Long userId = UserContext.getUserId();
        Long count = memberMapper.selectCount(
                new LambdaQueryWrapper<TeamMember>()
                        .eq(TeamMember::getTeamId, teamId)
                        .eq(TeamMember::getUserId, userId));
        if (count > 0) {
            throw new BusinessException("已在该队伍中（唯一键防重复）");
        }
        TeamMember member = new TeamMember();
        member.setTeamId(teamId);
        member.setUserId(userId);
        member.setRole("MEMBER");
        member.setJoinTime(LocalDateTime.now());
        memberMapper.insert(member);

        team.setMemberCount(team.getMemberCount() + 1);
        if (team.getMemberCount() >= team.getMaxMembers()) {
            team.setStatus("FULL");
        }
        teamMapper.updateById(team);
    }

    @Override
    @Transactional
    public void leaveTeam(Long teamId) {
        Long userId = UserContext.getUserId();
        TeamMember member = memberMapper.selectOne(
                new LambdaQueryWrapper<TeamMember>()
                        .eq(TeamMember::getTeamId, teamId)
                        .eq(TeamMember::getUserId, userId));
        if (member == null) {
            throw new BusinessException("您不在此队伍中");
        }
        if ("LEADER".equals(member.getRole())) {
            throw new BusinessException("队长不可退出，可解散队伍");
        }
        memberMapper.deleteById(member.getId());
        CompetitionTeam team = teamMapper.selectById(teamId);
        if (team != null) {
            team.setMemberCount(Math.max(0, team.getMemberCount() - 1));
            if ("FULL".equals(team.getStatus())) {
                team.setStatus("RECRUITING");
            }
            teamMapper.updateById(team);
        }
    }

    @Override
    public List<TeamMember> teamMembers(Long teamId) {
        return memberMapper.selectList(
                new LambdaQueryWrapper<TeamMember>()
                        .eq(TeamMember::getTeamId, teamId)
                        .orderByDesc(TeamMember::getJoinTime));
    }

    @Override
    public List<Map<String, Object>> myTeams(Long userId) {
        List<TeamMember> members = memberMapper.selectList(
                new LambdaQueryWrapper<TeamMember>().eq(TeamMember::getUserId, userId));
        return members.stream().map(m -> {
            CompetitionTeam team = teamMapper.selectById(m.getTeamId());
            Map<String, Object> map = new HashMap<>();
            map.put("teamId", m.getTeamId());
            map.put("teamName", team != null ? team.getName() : "已删除");
            map.put("role", m.getRole());
            map.put("joinTime", m.getJoinTime());
            return map;
        }).toList();
    }

    @Override
    public void updateStatus() {
        LocalDate today = LocalDate.now();
        List<Competition> all = competitionMapper.selectList(null);
        for (Competition c : all) {
            String target = null;
            if (c.getStartDate() != null && !today.isBefore(c.getStartDate())
                    && "RECRUITING".equals(c.getStatus())) {
                target = "IN_PROGRESS";
            }
            if (c.getEndDate() != null && today.isAfter(c.getEndDate())
                    && "IN_PROGRESS".equals(c.getStatus())) {
                target = "FINISHED";
            }
            if (target != null) {
                c.setStatus(target);
                competitionMapper.updateById(c);
            }
        }
    }
}
