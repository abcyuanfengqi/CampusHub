package com.campushub.modules.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.common.result.Result;
import com.campushub.common.utils.UserContext;
import com.campushub.modules.secondhand.entity.SecondhandItem;
import com.campushub.modules.secondhand.service.impl.SecondhandServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 二手闲置接口（M6）
 */
@RestController
@RequestMapping("/secondhand")
@RequiredArgsConstructor
public class SecondhandController {

    private final SecondhandServiceImpl secondhandService;

    /** 发布商品（ON_SALE） */
    @PostMapping
    public Result<SecondhandItem> publish(@RequestBody SecondhandItem item) {
        return Result.success(secondhandService.publish(item));
    }

    /** 分页浏览（排除下架） */
    @GetMapping
    public Result<Page<SecondhandItem>> page(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) String category) {
        return Result.success(secondhandService.page(page, size, keyword, category));
    }

    /** 详情 */
    @GetMapping("/{id}")
    public Result<SecondhandItem> detail(@PathVariable Long id) {
        return Result.success(secondhandService.get(id));
    }

    /** 预约（ON_SALE→RESERVED） */
    @PostMapping("/{id}/reserve")
    public Result<SecondhandItem> reserve(@PathVariable Long id) {
        return Result.success(secondhandService.reserve(id));
    }

    /** 确认成交（RESERVED→SOLD，异步通知双方） */
    @PostMapping("/{id}/sell")
    public Result<SecondhandItem> sell(@PathVariable Long id) {
        return Result.success(secondhandService.sell(id));
    }

    /** 下架（→OFF_SHELF） */
    @DeleteMapping("/{id}")
    public Result<SecondhandItem> offShelf(@PathVariable Long id) {
        return Result.success(secondhandService.offShelf(id));
    }

    /** 我发布的 */
    @GetMapping("/mine")
    public Result<Page<SecondhandItem>> mine(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return Result.success(secondhandService.mine(UserContext.getUserId(), page, size));
    }
}
