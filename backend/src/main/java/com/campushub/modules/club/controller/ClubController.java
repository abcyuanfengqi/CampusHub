package com.campushub.modules.club.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.common.annotation.RequireRole;
import com.campushub.common.result.Result;
import com.campushub.common.utils.UserContext;
import com.campushub.modules.club.entity.Club;
import com.campushub.modules.club.entity.ClubMember;
import com.campushub.modules.club.entity.ClubPost;
import com.campushub.modules.club.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 社团管理接口（M2）
 */
@RestController
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    /** 创建社团（状态 PENDING） */
    @PostMapping
    public Result<Club> create(@RequestBody Club club) {
        club.setLeaderId(UserContext.getUserId());
        return Result.success(clubService.createClub(club));
    }

    /** 管理员审批（状态机 PENDING→APPROVED/REJECTED） */
    @PutMapping("/{id}/approve")
    @RequireRole("ADMIN")
    public Result<Club> approve(@PathVariable Long id, @RequestParam boolean pass,
                                @RequestParam(required = false) String reason) {
        return Result.success(clubService.approve(id, pass, reason));
    }

    /** 解散社团（状态机→DISBANDED） */
    @PutMapping("/{id}/disband")
    public Result<Club> disband(@PathVariable Long id) {
        return Result.success(clubService.disband(id));
    }

    /** 更新社团资料（仅社长） */
    @PutMapping
    public Result<Club> update(@RequestBody Club club) {
        club.setLeaderId(UserContext.getUserId());
        return Result.success(clubService.updateClub(club));
    }

    /** 分页查询社团 */
    @GetMapping
    public Result<Page<Club>> page(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String category,
                                   @RequestParam(required = false) String status) {
        return Result.success(clubService.pageClubs(page, size, keyword, category, status));
    }

    /** 社团详情 */
    @GetMapping("/{id}")
    public Result<Club> detail(@PathVariable Long id) {
        return Result.success(clubService.getClub(id));
    }

    /** 我加入的社团 */
    @GetMapping("/mine")
    public Result<List<Club>> mine() {
        return Result.success(clubService.myClubs(UserContext.getUserId()));
    }

    /** 成员列表 */
    @GetMapping("/{id}/members")
    public Result<List<ClubMember>> members(@PathVariable Long id) {
        return Result.success(clubService.members(id));
    }

    /** 申请加入社团 */
    @PostMapping("/{id}/join")
    public Result<Void> join(@PathVariable Long id) {
        clubService.applyJoin(id);
        return Result.success();
    }

    /** 审批成员（社长） */
    @PutMapping("/{id}/members/{memberId}")
    public Result<Void> approveMember(@PathVariable Long id, @PathVariable Long memberId,
                                      @RequestParam boolean pass) {
        clubService.approveMember(id, memberId, pass);
        return Result.success();
    }

    /** 退出社团 */
    @DeleteMapping("/{id}/leave")
    public Result<Void> leave(@PathVariable Long id) {
        clubService.leave(id);
        return Result.success();
    }

    /** 发布社团动态 */
    @PostMapping("/{id}/post")
    public Result<ClubPost> publishPost(@PathVariable Long id, @RequestBody ClubPost post) {
        post.setClubId(id);
        post.setUserId(UserContext.getUserId());
        return Result.success(clubService.publishPost(post));
    }

    /** 社团动态列表 */
    @GetMapping("/{id}/posts")
    public Result<List<ClubPost>> posts(@PathVariable Long id) {
        return Result.success(clubService.posts(id));
    }

    /** 我的申请记录 */
    @GetMapping("/joins/mine")
    public Result<List<Map<String, Object>>> myJoins() {
        return Result.success(clubService.myJoins(UserContext.getUserId()));
    }
}
