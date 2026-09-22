package com.campushub.modules.user.service;

import com.campushub.modules.user.dto.LoginDTO;
import com.campushub.modules.user.dto.RegisterDTO;
import com.campushub.modules.user.vo.UserVO;

public interface UserService {
    UserVO login(LoginDTO dto);
    UserVO register(RegisterDTO dto);
    UserVO getCurrentUser();
}
