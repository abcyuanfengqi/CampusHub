package com.campushub.modules.club.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.modules.club.entity.Club;
import com.campushub.modules.club.entity.ClubMember;
import com.campushub.modules.club.entity.ClubPost;

import java.util.List;
import java.util.Map;

public interface ClubService {
    Club createClub(Club club);                        // 创建社团（状态PENDING）
    Club approve(Long clubId, boolean pass, String reason); // 管理员审批（状态机）
    Club disband(Long clubId);                         // 解散（状态机）
    Club updateClub(Club club);                        // 更新资料（仅社长）
    Page<Club> pageClubs(int page, int size, String keyword, String category, String status);
    Club getClub(Long id);
    List<Club> myClubs(Long userId);                   // 我加入的社团
    List<ClubMember> members(Long clubId);             // 成员列表
    void applyJoin(Long clubId);                       // 申请入社
    void approveMember(Long clubId, Long memberId, boolean pass); // 审批成员（社长）
    void leave(Long clubId);                           // 退出社团
    ClubPost publishPost(ClubPost post);               // 发布动态
    List<ClubPost> posts(Long clubId);                 // 社团动态
    List<Map<String, Object>> myJoins(Long userId);    // 我的申请记录
}
