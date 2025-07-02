package com.ai.admin.controller;

import com.ai.admin.common.Result;
import com.ai.admin.entity.SysUser;
import com.ai.admin.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户登录注册相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private SysUserService sysUserService;
    
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = sysUserService.login(request.getUsername(), request.getPassword());
            return Result.success("登录成功", token);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterRequest request) {
        try {
            SysUser user = new SysUser();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            user.setNickname(request.getNickname());
            
            sysUserService.register(user);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        
        @NotBlank(message = "密码不能为空")
        private String password;
    }
    
    @Data
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        
        @NotBlank(message = "密码不能为空")
        private String password;
        
        @NotBlank(message = "邮箱不能为空")
        private String email;
        
        private String nickname;
    }
}