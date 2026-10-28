package com.campushub.modules.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.common.event.EventPublisher;
import com.campushub.common.exception.BusinessException;
import com.campushub.modules.club.entity.Club;
import com.campushub.modules.club.entity.ClubMember;
import com.campushub.modules.club.entity.ClubPost;
import com.campushub.modules.club.mapper.ClubMapper;
import com.campushub.modules.club.mapper.ClubMemberMapper;
import com.campushub.modules.club.mapper.ClubPostMapper;
import com.campushub.modules.club.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社团模块服务（M2）
 * 状态机：PENDING → APPROVED / REJECTED → DISBANDED
 */
@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final ClubMapper clubMapper;
    private final ClubMemberMapper clubMemberMapper;
    private final ClubPostMapper clubPostMapper;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public Club createClub(Club club) {
        Long count = clubMapper.selectCount(
                new LambdaQueryWrapper<Club>().eq(Club::getName, club.getName()));
        if (count > 0) {
            throw new BusinessException("社团名称已存在");
        }
        club.setLeaderId(club.getLeaderId());
        club.setMemberCount(1);
        club.setStatus("PENDING");
        club.setCreateTime(LocalDateTime.now());
        club.setUpdateTime(LocalDateTime.now());
        clubMapper.insert(club);
        // 创建人自动成为社长成员
        ClubMember leader = new ClubMember();
        leader.setClubId(club.getId());
        leader.setUserId(club.getLeaderId());
        leader.setRole("LEADER");
        leader.setStatus("APPROVED");
        leader.setJoinTime(LocalDateTime.now());
        clubMemberMapper.insert(leader);
        return club;
    }

    @Override
    @Transactional
    public Club approve(Long clubId, boolean pass, String reason) {
        Club club = getClub(clubId);
        if (!"PENDING".equals(club.getStatus())) {
            throw new BusinessException("当前状态不允许审批（仅 PENDING 可审批）");
        }
        club.setStatus(pass ? "APPROVED" : "REJECTED");
        club.setUpdateTime(LocalDateTime.now());
        clubMapper.updateById(club);
        // 异步通知社长审批结果
        Map<String, Object> payload = new HashMap<>();
        payload.put("clubId", clubId);
        payload.put("clubName", club.getName());
        payload.put("leaderId", club.getLeaderId());
        payload.put("result", pass ? "APPROVED" : "REJECTED");
        payload.put("reason", reason);
        eventPublisher.publish("CLUB_APPROVED_EVENT", club.getLeaderId(), payload);
        return club;
    }

    @Override
    @Transactional
    public Club disband(Long clubId) {
        Club club = getClub(clubId);
        if ("DISBANDED".equals(club.getStatus())) {
            throw new BusinessException("社团已解散");
        }
        club.setStatus("DISBANDED");
        club.setUpdateTime(LocalDateTime.now());
        clubMapper.updateById(club);
        return club;
    }

    @Override
    @Transactional
    public Club updateClub(Club club) {
        Club exist = getClub(club.getId());
        if (!exist.getLeaderId().equals(club.getLeaderId())) {
            throw new BusinessException("仅社长可修改社团资料");
        }
        if ("DISBANDED".equals(exist.getStatus())) {
            throw new BusinessException("社团已解散");
        }
        club.setUpdateTime(LocalDateTime.now());
        clubMapper.updateById(club);
        return club;
    }

    @Override
    public Page<Club> pageClubs(int page, int size, String keyword, String category, String status) {
        LambdaQueryWrapper<Club> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like(Club::getName, keyword);
        }
        if (StringUtils.hasText(category)) {
            qw.eq(Club::getCategory, category);
        }
        if (StringUtils.hasText(status)) {
            qw.eq(Club::getStatus, status);
        } else {
            qw.ne(Club::getStatus, "REJECTED").ne(Club::getStatus, "DISBANDED");
        }
        qw.orderByDesc(Club::getCreateTime);
        return clubMapper.selectPage(new Page<>(page, size), qw);
    }

    @Override
    public Club getClub(Long id) {
        Club club = clubMapper.selectById(id);
        if (club == null) {
            throw new BusinessException("社团不存在");
        }
        return club;
    }

    @Override
    public List<Club> myClubs(Long userId) {
        List<ClubMember> members = clubMemberMapper.selectList(
                new LambdaQueryWrapper<ClubMember>()
                        .eq(ClubMember::getUserId, userId)
                        .eq(ClubMember::getStatus, "APPROVED"));
        if (members.isEmpty()) {
            return List.of();
        }
        List<Long> ids = members.stream().map(ClubMember::getClubId).toList();
        return clubMapper.selectBatchIds(ids);
    }

    @Override
    public List<ClubMember> members(Long clubId) {
        return clubMemberMapper.selectList(
                new LambdaQueryWrapper<ClubMember>()
                        .eq(ClubMember::getClubId, clubId)
                        .orderByDesc(ClubMember::getJoinTime));
    }

    @Override
    @Transactional
    public void applyJoin(Long clubId) {
        Club club = getClub(clubId);
        if (!"APPROVED".equals(club.getStatus())) {
            throw new BusinessException("社团未通过审批，暂不能加入");
        }
        Long userId = com.campushub.common.utils.UserContext.getUserId();
        Long count = clubMemberMapper.selectCount(
                new LambdaQueryWrapper<ClubMember>()
                        .eq(ClubMember::getClubId, clubId)
                        .eq(ClubMember::getUserId, userId));
        if (count > 0) {
            throw new BusinessException("已申请过该社团（唯一键防重复）");
        }
        ClubMember member = new ClubMember();
        member.setClubId(clubId);
        member.setUserId(userId);
        member.setRole("MEMBER");
        member.setStatus("APPROVED");
        member.setJoinTime(LocalDateTime.now());
        clubMemberMapper.insert(member);
        club.setMemberCount(club.getMemberCount() + 1);
        club.setUpdateTime(LocalDateTime.now());
        clubMapper.updateById(club);
    }

    @Override
    @Transactional
    public void approveMember(Long clubId, Long memberId, boolean pass) {
        ClubMember member = clubMemberMapper.selectById(memberId);
        if (member == null || !member.getClubId().equals(clubId)) {
            throw new BusinessException("成员记录不存在");
        }
        if ("LEADER".equals(member.getRole())) {
            throw new BusinessException("社长不可被移除");
        }
        member.setStatus(pass ? "APPROVED" : "REJECTED");
        clubMemberMapper.updateById(member);
    }

    @Override
    @Transactional
    public void leave(Long clubId) {
        Long userId = com.campushub.common.utils.UserContext.getUserId();
        clubMemberMapper.delete(
                new LambdaQueryWrapper<ClubMember>()
                        .eq(ClubMember::getClubId, clubId)
                        .eq(ClubMember::getUserId, userId));
        Club club = getClub(clubId);
        if (club.getMemberCount() > 1) {
            club.setMemberCount(club.getMemberCount() - 1);
            club.setUpdateTime(LocalDateTime.now());
            clubMapper.updateById(club);
        }
    }

    @Override
    public ClubPost publishPost(ClubPost post) {
        post.setCreateTime(LocalDateTime.now());
        clubPostMapper.insert(post);
        return post;
    }

    @Override
    public List<ClubPost> posts(Long clubId) {
        return clubPostMapper.selectList(
                new LambdaQueryWrapper<ClubPost>()
                        .eq(ClubPost::getClubId, clubId)
                        .orderByDesc(ClubPost::getCreateTime));
    }

    @Override
    public List<Map<String, Object>> myJoins(Long userId) {
        List<ClubMember> members = clubMemberMapper.selectList(
                new LambdaQueryWrapper<ClubMember>().eq(ClubMember::getUserId, userId));
        return members.stream().map(m -> {
            Club club = clubMapper.selectById(m.getClubId());
            Map<String, Object> map = new HashMap<>();
            map.put("clubId", m.getClubId());
            map.put("clubName", club != null ? club.getName() : "已删除");
            map.put("role", m.getRole());
            map.put("status", m.getStatus());
            map.put("joinTime", m.getJoinTime());
            return map;
        }).toList();
    }
}
