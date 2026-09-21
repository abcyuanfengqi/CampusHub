package com.campushub.modules.user.controller;

import com.campushub.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统测试接口
 *
 * @author campushub
 */
@Tag(name = "系统接口", description = "系统健康检查、测试接口")
@RestController
@RequestMapping("/system")
public class SystemController {

    @Operation(summary = "健康检查", description = "检查系统是否正常运行")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("app", "CampusHub 校园服务中心");
        result.put("version", "1.0.0");
        return Result.success(result);
    }

}
