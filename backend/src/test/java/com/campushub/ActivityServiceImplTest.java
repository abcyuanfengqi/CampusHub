package com.campushub;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campushub.common.event.EventPublisher;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.utils.UserContext;
import com.campushub.modules.activity.entity.Activity;
import com.campushub.modules.activity.entity.ActivityRegistration;
import com.campushub.modules.activity.mapper.ActivityCheckinMapper;
import com.campushub.modules.activity.mapper.ActivityMapper;
import com.campushub.modules.activity.mapper.ActivityRegistrationMapper;
import com.campushub.modules.activity.service.impl.ActivityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 活动模块单元测试（状态机 + Redis并发控制 R7）
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ActivityServiceImplTest {

    @Mock ActivityMapper activityMapper;
    @Mock ActivityRegistrationMapper registrationMapper;
    @Mock ActivityCheckinMapper checkinMapper;
    @Mock StringRedisTemplate redisTemplate;
    @Mock EventPublisher eventPublisher;
    @Mock ValueOperations<String, String> valueOperations;

    private ActivityServiceImpl activityService;

    @BeforeEach
    void setUp() {
        activityService = new ActivityServiceImpl(activityMapper, registrationMapper, checkinMapper,
                redisTemplate, eventPublisher);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        UserContext.set(2L, "student", "STUDENT");
    }

    private Activity buildRegisteringActivity(int max) {
        Activity a = new Activity();
        a.setId(1L);
        a.setTitle("篮球赛");
        a.setStatus("REGISTERING");
        a.setMaxParticipants(max);
        a.setCurrentParticipants(0);
        return a;
    }

    @Test
    void 报名成功返回REGISTERED并发布事件() {
        when(activityMapper.selectById(1L)).thenReturn(buildRegisteringActivity(100));
        when(registrationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(valueOperations.increment(anyString())).thenReturn(1L);
        when(registrationMapper.insert(any(ActivityRegistration.class))).thenReturn(1);

        var result = activityService.register(1L);
        assertEquals("REGISTERED", result.get("status"));
        verify(eventPublisher, times(1)).publish(eq("REGISTRATION_EVENT"), eq(2L), any());
    }

    @Test
    void 活动非REGISTERING状态报名被拒绝() {
        Activity a = buildRegisteringActivity(100);
        a.setStatus("DRAFT");
        when(activityMapper.selectById(1L)).thenReturn(a);
        assertThrows(BusinessException.class, () -> activityService.register(1L));
        verify(valueOperations, never()).increment(anyString());
    }

    @Test
    void 名额耗尽报名被拒绝并回滚计数() {
        when(activityMapper.selectById(1L)).thenReturn(buildRegisteringActivity(2));
        when(registrationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(valueOperations.increment(anyString())).thenReturn(3L);  // 超上限
        assertThrows(BusinessException.class, () -> activityService.register(1L));
        verify(valueOperations, times(1)).decrement(anyString());
    }

    @Test
    void 重复报名被唯一键逻辑拦截() {
        when(activityMapper.selectById(1L)).thenReturn(buildRegisteringActivity(100));
        when(registrationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);
        assertThrows(BusinessException.class, () -> activityService.register(1L));
    }

    @Test
    void 未报名签到被拒绝() {
        Activity a = buildRegisteringActivity(100);
        a.setStatus("IN_PROGRESS");
        when(activityMapper.selectById(1L)).thenReturn(a);
        when(registrationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        assertThrows(BusinessException.class, () -> activityService.checkIn(1L, "code123"));
    }

    @Test
    void 已签到重复签到被拒绝() {
        Activity a = buildRegisteringActivity(100);
        a.setStatus("IN_PROGRESS");
        when(activityMapper.selectById(1L)).thenReturn(a);
        when(registrationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);
        when(checkinMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);
        assertThrows(BusinessException.class, () -> activityService.checkIn(1L, "code123"));
    }
}
