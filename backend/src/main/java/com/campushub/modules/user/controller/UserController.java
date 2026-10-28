package com.campushub.modules.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.common.annotation.RequireRole;
import com.campushub.common.result.Result;
import com.campushub.modules.user.entity.User;
import com.campushub.modules.user.service.UserService;
import com.campushub.modules.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理接口（M1：个人信息 + 管理员用户管理）
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 当前登录用户信息 */
    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.success(userService.getCurrentUser());
    }

    /** 用户详情（含管理员查看） */
    @GetMapping("/{id}")
    public Result<User> detail(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    /** 修改个人信息（昵称/邮箱/手机号/头像） */
    @PutMapping("/me")
    public Result<UserVO> updateProfile(@RequestBody User user) {
        return Result.success(userService.updateProfile(user));
    }

    /** 用户分页（管理员） */
    @GetMapping
    @RequireRole("ADMIN")
    public Result<Page<User>> page(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String keyword) {
        return Result.success(userService.pageUsers(page, size, keyword));
    }

    /** 启用/禁用用户（管理员） */
    @PutMapping("/{id}/status")
    @RequireRole("ADMIN")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.changeStatus(id, status);
        return Result.success();
    }

    /** 修改角色（管理员） */
    @PutMapping("/{id}/role")
    @RequireRole("ADMIN")
    public Result<Void> changeRole(@PathVariable Long id, @RequestParam String role) {
        userService.changeRole(id, role);
        return Result.success();
    }
}
