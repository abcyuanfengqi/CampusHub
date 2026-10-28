package com.campushub.modules.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.modules.user.dto.LoginDTO;
import com.campushub.modules.user.dto.RegisterDTO;
import com.campushub.modules.user.entity.User;
import com.campushub.modules.user.vo.UserVO;

public interface UserService {
    UserVO login(LoginDTO dto);
    UserVO register(RegisterDTO dto);
    UserVO getCurrentUser();
    User getUserById(Long id);
    UserVO updateProfile(User user);
    Page<User> pageUsers(int page, int size, String keyword);
    void changeStatus(Long userId, Integer status);
    void changeRole(Long userId, String role);
}
