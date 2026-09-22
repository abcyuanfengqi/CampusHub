package com.campushub.modules.user.controller;

import com.campushub.common.result.Result;
import com.campushub.modules.user.dto.LoginDTO;
import com.campushub.modules.user.dto.RegisterDTO;
import com.campushub.modules.user.service.UserService;
import com.campushub.modules.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO dto) {
        return Result.success(userService.register(dto));
    }
}
