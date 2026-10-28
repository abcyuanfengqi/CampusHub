package com.campushub.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.utils.JwtUtils;
import com.campushub.modules.user.dto.LoginDTO;
import com.campushub.modules.user.dto.RegisterDTO;
import com.campushub.modules.user.entity.User;
import com.campushub.modules.user.mapper.UserMapper;
import com.campushub.modules.user.service.UserService;
import com.campushub.modules.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;

    @Override
    public UserVO login(LoginDTO dto) {
        // 1. 查询用户
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 2. 校验密码（MD5）
        String md5Password = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes());
        if (!md5Password.equals(user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        // 3. 检查状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 4. 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 5. 返回VO
        return convertToVO(user, token);
    }

    @Override
    public UserVO register(RegisterDTO dto) {
        // 1. 检查用户名是否已存在
        Long count = userMapper.selectCount(
            new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        // 2. 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes()));
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("STUDENT");
        user.setStatus(1);
        
        userMapper.insert(user);
        
        // 3. 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        return convertToVO(user, token);
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = com.campushub.common.utils.UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user, null);
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public UserVO updateProfile(User user) {
        Long userId = com.campushub.common.utils.UserContext.getUserId();
        User exist = userMapper.selectById(userId);
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }
        user.setId(userId);
        user.setPassword(null);          // 不允许通过此接口改密码
        user.setUsername(null);          // 用户名不可改
        user.setRole(null);
        user.setStatus(null);
        userMapper.updateById(user);
        return convertToVO(userMapper.selectById(userId), null);
    }

    @Override
    public Page<User> pageUsers(int page, int size, String keyword) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User> qw =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (org.springframework.util.StringUtils.hasText(keyword)) {
            qw.like(User::getUsername, keyword).or().like(User::getNickname, keyword);
        }
        qw.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size), qw);
    }

    @Override
    public void changeStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void changeRole(Long userId, String role) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setRole(role);
        userMapper.updateById(user);
    }

    private UserVO convertToVO(User user, String token) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setToken(token);
        return vo;
    }
}
