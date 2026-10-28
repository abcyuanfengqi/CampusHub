package com.campushub;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campushub.common.event.EventPublisher;
import com.campushub.common.exception.BusinessException;
import com.campushub.modules.club.entity.Club;
import com.campushub.modules.club.entity.ClubMember;
import com.campushub.modules.club.mapper.ClubMapper;
import com.campushub.modules.club.mapper.ClubMemberMapper;
import com.campushub.modules.club.mapper.ClubPostMapper;
import com.campushub.modules.club.service.impl.ClubServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 社团模块单元测试（状态机 C2）
 */
@ExtendWith(MockitoExtension.class)
class ClubServiceImplTest {

    @Mock ClubMapper clubMapper;
    @Mock ClubMemberMapper clubMemberMapper;
    @Mock ClubPostMapper clubPostMapper;
    @Mock EventPublisher eventPublisher;

    private ClubServiceImpl clubService;

    @BeforeEach
    void setUp() {
        clubService = new ClubServiceImpl(clubMapper, clubMemberMapper, clubPostMapper, eventPublisher);
    }

    @Test
    void 创建社团初始状态为PENDING() {
        when(clubMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        Club club = new Club();
        club.setName("计算机协会");
        club.setLeaderId(1L);
        Club created = clubService.createClub(club);
        assertEquals("PENDING", created.getStatus());
        assertEquals(Integer.valueOf(1), created.getMemberCount());
        verify(clubMapper, times(1)).insert(any(Club.class));
        verify(clubMemberMapper, times(1)).insert(any(ClubMember.class));
    }

    @Test
    void 社团重名创建被拒绝() {
        when(clubMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);
        Club club = new Club();
        club.setName("计算机协会");
        assertThrows(BusinessException.class, () -> clubService.createClub(club));
    }

    @Test
    void 管理员审批通过后状态APPROVED() {
        Club club = new Club();
        club.setId(1L);
        club.setName("计算机协会");
        club.setLeaderId(1L);
        club.setStatus("PENDING");
        when(clubMapper.selectById(1L)).thenReturn(club);
        Club approved = clubService.approve(1L, true, "同意");
        assertEquals("APPROVED", approved.getStatus());
        verify(eventPublisher, times(1)).publish(eq("CLUB_APPROVED_EVENT"), any(), any());
    }

    @Test
    void 非PENDING状态审批被拒绝() {
        Club club = new Club();
        club.setId(1L);
        club.setStatus("APPROVED");
        when(clubMapper.selectById(1L)).thenReturn(club);
        assertThrows(BusinessException.class, () -> clubService.approve(1L, true, null));
    }

    @Test
    void 解散后状态为DISBANDED() {
        Club club = new Club();
        club.setId(1L);
        club.setStatus("APPROVED");
        when(clubMapper.selectById(1L)).thenReturn(club);
        Club disbanded = clubService.disband(1L);
        assertEquals("DISBANDED", disbanded.getStatus());
    }
}
