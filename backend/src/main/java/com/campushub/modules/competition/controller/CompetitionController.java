package com.campushub.modules.competition.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.common.result.Result;
import com.campushub.common.utils.UserContext;
import com.campushub.modules.competition.entity.Competition;
import com.campushub.modules.competition.entity.CompetitionTeam;
import com.campushub.modules.competition.entity.TeamMember;
import com.campushub.modules.competition.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 竞赛组队接口（M3）
 */
@RestController
@RequestMapping("/competition")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

    /** 发布竞赛（状态 RECRUITING） */
    @PostMapping
    public Result<Competition> create(@RequestBody Competition c) {
        return Result.success(competitionService.createCompetition(c));
    }

    /** 分页查询竞赛 */
    @GetMapping
    public Result<Page<Competition>> page(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String level) {
        return Result.success(competitionService.pageCompetitions(page, size, keyword, level));
    }

    /** 竞赛详情 */
    @GetMapping("/{id}")
    public Result<Competition> detail(@PathVariable Long id) {
        return Result.success(competitionService.getCompetition(id));
    }

    /** 创建队伍 */
    @PostMapping("/{id}/team")
    public Result<CompetitionTeam> createTeam(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String name = (String) body.getOrDefault("name", "未命名队伍");
        String desc = (String) body.getOrDefault("description", "");
        int max = body.get("maxMembers") == null ? 5 : Integer.parseInt(body.get("maxMembers").toString());
        return Result.success(competitionService.createTeam(id, name, desc, max));
    }

    /** 队伍列表 */
    @GetMapping("/{id}/teams")
    public Result<List<CompetitionTeam>> teams(@PathVariable Long id) {
        return Result.success(competitionService.teams(id));
    }

    /** 加入队伍（满员拒绝，唯一键防重复） */
    @PostMapping("/team/{teamId}/join")
    public Result<Void> join(@PathVariable Long teamId) {
        competitionService.joinTeam(teamId);
        return Result.success();
    }

    /** 退出队伍 */
    @DeleteMapping("/team/{teamId}/leave")
    public Result<Void> leave(@PathVariable Long teamId) {
        competitionService.leaveTeam(teamId);
        return Result.success();
    }

    /** 队伍成员 */
    @GetMapping("/team/{teamId}/members")
    public Result<List<TeamMember>> members(@PathVariable Long teamId) {
        return Result.success(competitionService.teamMembers(teamId));
    }

    /** 我的队伍 */
    @GetMapping("/mine")
    public Result<List<Map<String, Object>>> mine() {
        return Result.success(competitionService.myTeams(UserContext.getUserId()));
    }
}
