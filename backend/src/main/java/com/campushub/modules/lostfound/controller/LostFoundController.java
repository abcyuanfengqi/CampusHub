package com.campushub.modules.lostfound.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.common.result.Result;
import com.campushub.common.utils.UserContext;
import com.campushub.modules.lostfound.entity.LostFound;
import com.campushub.modules.lostfound.service.impl.LostFoundServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 失物招领接口（M7）
 */
@RestController
@RequestMapping("/lostfound")
@RequiredArgsConstructor
public class LostFoundController {

    private final LostFoundServiceImpl lostFoundService;

    /** 发布失物/招领（PENDING） */
    @PostMapping
    public Result<LostFound> publish(@RequestBody LostFound lf) {
        return Result.success(lostFoundService.publish(lf));
    }

    /** 分页查询（type=LOST/FOUND） */
    @GetMapping
    public Result<Page<LostFound>> page(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String type,
                                        @RequestParam(required = false) String keyword) {
        return Result.success(lostFoundService.page(page, size, type, keyword));
    }

    /** 详情 */
    @GetMapping("/{id}")
    public Result<LostFound> detail(@PathVariable Long id) {
        return Result.success(lostFoundService.get(id));
    }

    /** 认领（PENDING→CLAIMED） */
    @PostMapping("/{id}/claim")
    public Result<LostFound> claim(@PathVariable Long id) {
        return Result.success(lostFoundService.claim(id));
    }

    /** 核实结案（→RESOLVED） */
    @PostMapping("/{id}/resolve")
    public Result<LostFound> resolve(@PathVariable Long id) {
        return Result.success(lostFoundService.resolve(id));
    }

    /** 我发布的 */
    @GetMapping("/mine")
    public Result<Page<LostFound>> mine(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return Result.success(lostFoundService.mine(UserContext.getUserId(), page, size));
    }
}
