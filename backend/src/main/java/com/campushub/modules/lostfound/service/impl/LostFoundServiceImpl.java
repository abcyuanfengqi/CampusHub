package com.campushub.modules.lostfound.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.utils.UserContext;
import com.campushub.modules.lostfound.entity.LostFound;
import com.campushub.modules.lostfound.mapper.LostFoundMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 失物招领服务（M7）
 * 状态机: PENDING → CLAIMED → RESOLVED；PENDING → RESOLVED
 */
@Service
@RequiredArgsConstructor
public class LostFoundServiceImpl {

    private final LostFoundMapper lostFoundMapper;

    public LostFound publish(LostFound lf) {
        lf.setPublisherId(UserContext.getUserId());
        lf.setStatus("PENDING");
        lf.setCreateTime(LocalDateTime.now());
        lf.setUpdateTime(LocalDateTime.now());
        lostFoundMapper.insert(lf);
        return lf;
    }

    public Page<LostFound> page(int page, int size, String type, String keyword) {
        LambdaQueryWrapper<LostFound> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) {
            qw.eq(LostFound::getType, type);
        }
        if (StringUtils.hasText(keyword)) {
            qw.like(LostFound::getTitle, keyword);
        }
        qw.orderByDesc(LostFound::getCreateTime);
        return lostFoundMapper.selectPage(new Page<>(page, size), qw);
    }

    public LostFound get(Long id) {
        LostFound lf = lostFoundMapper.selectById(id);
        if (lf == null) {
            throw new BusinessException("记录不存在");
        }
        return lf;
    }

    /** 认领（PENDING→CLAIMED，记录认领人） */
    @Transactional
    public LostFound claim(Long id) {
        LostFound lf = get(id);
        if (!"PENDING".equals(lf.getStatus())) {
            throw new BusinessException("该记录已处理");
        }
        lf.setStatus("CLAIMED");
        lf.setFinderId(UserContext.getUserId());
        lf.setUpdateTime(LocalDateTime.now());
        lostFoundMapper.updateById(lf);
        return lf;
    }

    /** 核实结案（CLAIMED→RESOLVED 或 PENDING→RESOLVED） */
    @Transactional
    public LostFound resolve(Long id) {
        LostFound lf = get(id);
        if (!"PENDING".equals(lf.getStatus()) && !"CLAIMED".equals(lf.getStatus())) {
            throw new BusinessException("当前状态不可结案");
        }
        lf.setStatus("RESOLVED");
        lf.setUpdateTime(LocalDateTime.now());
        lostFoundMapper.updateById(lf);
        return lf;
    }

    public Page<LostFound> mine(Long userId, int page, int size) {
        return lostFoundMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<LostFound>()
                        .eq(LostFound::getPublisherId, userId)
                        .orderByDesc(LostFound::getCreateTime));
    }
}
