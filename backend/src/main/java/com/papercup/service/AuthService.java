package com.papercup.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.papercup.dto.AuthResponse;
import com.papercup.dto.LoginRequest;
import com.papercup.dto.RegisterRequest;
import com.papercup.entity.User;
import com.papercup.mapper.UserMapper;
import com.papercup.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * 用户注册
     */
    public AuthResponse register(RegisterRequest request) {
        // 检查密码确认
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameQuery = new LambdaQueryWrapper<>();
        usernameQuery.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(usernameQuery) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            LambdaQueryWrapper<User> emailQuery = new LambdaQueryWrapper<>();
            emailQuery.eq(User::getEmail, request.getEmail());
            if (userMapper.selectCount(emailQuery) > 0) {
                throw new RuntimeException("邮箱已被使用");
            }
        }

        // 检查手机号是否已存在
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            LambdaQueryWrapper<User> phoneQuery = new LambdaQueryWrapper<>();
            phoneQuery.eq(User::getPhone, request.getPhone());
            if (userMapper.selectCount(phoneQuery) > 0) {
                throw new RuntimeException("手机号已被使用");
            }
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRealName(request.getRealName());
        user.setRole("USER");
        user.setStatus(1);

        userMapper.insert(user);

        // 生成JWT令牌
        String token = jwtUtils.generateToken(user.getUsername(), user.getId(), user.getRole());

        // 构建响应
        AuthResponse response = new AuthResponse(token, user.getId(), user.getUsername(), user.getRole());
        response.setRealName(user.getRealName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());

        log.info("用户注册成功: {}", user.getUsername());
        return response;
    }

    /**
     * 用户登录
     */
    public AuthResponse login(LoginRequest request) {
        // 查找用户
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(query);

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户账号已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成JWT令牌
        String token = jwtUtils.generateToken(user.getUsername(), user.getId(), user.getRole());

        // 构建响应
        AuthResponse response = new AuthResponse(token, user.getId(), user.getUsername(), user.getRole());
        response.setRealName(user.getRealName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());

        log.info("用户登录成功: {}", user.getUsername());
        return response;
    }

    /**
     * 获取当前用户信息
     */
    public User getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 清空密码字段
        user.setPassword(null);
        return user;
    }
}
