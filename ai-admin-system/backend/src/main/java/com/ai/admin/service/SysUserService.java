package com.ai.admin.service;

import com.ai.admin.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 根据用户名查询用户
     */
    SysUser findByUsername(String username);
    
    /**
     * 用户登录
     */
    String login(String username, String password);
    
    /**
     * 用户注册
     */
    boolean register(SysUser user);
}