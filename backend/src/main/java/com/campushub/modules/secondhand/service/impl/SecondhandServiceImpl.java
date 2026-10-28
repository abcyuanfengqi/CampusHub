package com.campushub.modules.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.common.event.EventPublisher;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.utils.UserContext;
import com.campushub.modules.secondhand.entity.SecondhandItem;
import com.campushub.modules.secondhand.mapper.SecondhandItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 二手闲置服务（M6）
 * 状态机: ON_SALE → RESERVED → SOLD；ON_SALE/RESERVED → OFF_SHELF
 */
@Service
@RequiredArgsConstructor
public class SecondhandServiceImpl {

    private final SecondhandItemMapper itemMapper;
    private final EventPublisher eventPublisher;

    public SecondhandItem publish(SecondhandItem item) {
        item.setSellerId(UserContext.getUserId());
        item.setStatus("ON_SALE");
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        itemMapper.insert(item);
        return item;
    }

    public Page<SecondhandItem> page(int page, int size, String keyword, String category) {
        LambdaQueryWrapper<SecondhandItem> qw = new LambdaQueryWrapper<>();
        qw.ne(SecondhandItem::getStatus, "OFF_SHELF");
        if (StringUtils.hasText(keyword)) {
            qw.like(SecondhandItem::getTitle, keyword);
        }
        if (StringUtils.hasText(category)) {
            qw.eq(SecondhandItem::getCategory, category);
        }
        qw.orderByDesc(SecondhandItem::getCreateTime);
        return itemMapper.selectPage(new Page<>(page, size), qw);
    }

    public SecondhandItem get(Long id) {
        SecondhandItem item = itemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException("商品不存在");
        }
        return item;
    }

    @Transactional
    public SecondhandItem reserve(Long id) {
        SecondhandItem item = get(id);
        if (!"ON_SALE".equals(item.getStatus())) {
            throw new BusinessException("商品已不在售（状态: " + item.getStatus() + "）");
        }
        item.setStatus("RESERVED");
        item.setBuyerId(UserContext.getUserId());
        item.setUpdateTime(LocalDateTime.now());
        itemMapper.updateById(item);
        return item;
    }

    @Transactional
    public SecondhandItem sell(Long id) {
        SecondhandItem item = get(id);
        if (!"RESERVED".equals(item.getStatus())) {
            throw new BusinessException("仅被预约的商品可确认成交");
        }
        item.setStatus("SOLD");
        item.setUpdateTime(LocalDateTime.now());
        itemMapper.updateById(item);
        // 异步通知买卖双方（ADR-006）
        Map<String, Object> payload = new HashMap<>();
        payload.put("itemId", id);
        payload.put("title", item.getTitle());
        payload.put("sellerId", item.getSellerId());
        payload.put("buyerId", item.getBuyerId());
        eventPublisher.publish("TRADE_EVENT", item.getSellerId(), payload);
        return item;
    }

    @Transactional
    public SecondhandItem offShelf(Long id) {
        SecondhandItem item = get(id);
        if (!"ON_SALE".equals(item.getStatus()) && !"RESERVED".equals(item.getStatus())) {
            throw new BusinessException("当前状态不可下架");
        }
        item.setStatus("OFF_SHELF");
        item.setUpdateTime(LocalDateTime.now());
        itemMapper.updateById(item);
        return item;
    }

    public Page<SecondhandItem> mine(Long userId, int page, int size) {
        return itemMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<SecondhandItem>()
                        .eq(SecondhandItem::getSellerId, userId)
                        .orderByDesc(SecondhandItem::getCreateTime));
    }
}
