package com.campushub.modules.stat.controller;

import com.campushub.common.annotation.RequireRole;
import com.campushub.common.result.Result;
import com.campushub.modules.stat.service.impl.StatServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 数据看板接口（M8，管理员）
 */
@RestController
@RequestMapping("/stat")
@RequiredArgsConstructor
public class StatController {

    private final StatServiceImpl statService;

    @GetMapping("/dashboard")
    @RequireRole("ADMIN")
    public Result<Map<String, Object>> dashboard(@RequestParam(defaultValue = "30") int days) {
        return Result.success(statService.dashboard(days));
    }
}
