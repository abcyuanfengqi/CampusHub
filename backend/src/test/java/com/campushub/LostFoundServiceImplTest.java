package com.campushub;

import com.campushub.common.exception.BusinessException;
import com.campushub.common.utils.UserContext;
import com.campushub.modules.lostfound.entity.LostFound;
import com.campushub.modules.lostfound.mapper.LostFoundMapper;
import com.campushub.modules.lostfound.service.impl.LostFoundServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 失物招领模块单元测试（状态机 C2）
 */
@ExtendWith(MockitoExtension.class)
class LostFoundServiceImplTest {

    @Mock LostFoundMapper lostFoundMapper;

    private LostFoundServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new LostFoundServiceImpl(lostFoundMapper);
        UserContext.set(3L, "student", "STUDENT");
    }

    private LostFound buildPending() {
        LostFound lf = new LostFound();
        lf.setId(1L);
        lf.setType("LOST");
        lf.setTitle("蓝色校园卡");
        lf.setStatus("PENDING");
        return lf;
    }

    @Test
    void 认领后状态CLAIMED并记录认领人() {
        when(lostFoundMapper.selectById(1L)).thenReturn(buildPending());
        LostFound claimed = service.claim(1L);
        assertEquals("CLAIMED", claimed.getStatus());
        assertEquals(3L, claimed.getFinderId());
    }

    @Test
    void 已处理记录不可重复认领() {
        LostFound lf = buildPending();
        lf.setStatus("CLAIMED");
        when(lostFoundMapper.selectById(1L)).thenReturn(lf);
        assertThrows(BusinessException.class, () -> service.claim(1L));
    }

    @Test
    void 未找到直接结案PENDING到RESOLVED() {
        when(lostFoundMapper.selectById(1L)).thenReturn(buildPending());
        LostFound resolved = service.resolve(1L);
        assertEquals("RESOLVED", resolved.getStatus());
    }
}
