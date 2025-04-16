package com.cuiyq.cloudpicturebackend.service;

import com.cuiyq.cloudpicturebackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cuiyq.cloudpicturebackend.model.vo.LoginUserVo;

import javax.servlet.http.HttpServletRequest;

/**
* @author cuiyq
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-04-16 16:12:40
*/
public interface userService extends IService<User> {

    /**
     * 获取当前登录用户信息
     * @param request
     * @return 用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 获取加密后的密码
     * @param userPassword 源密码
     * @return 加密后的密码
     */
    String getEncryptByPassword(String userPassword);


    /**
     * 用户登录
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    LoginUserVo userLogin(String userAccount, String userPassword);

    /**
     * 获取脱敏后的用户信息
     * @param user 原始用户信息
     * @return 脱敏后的用户信息
     */
    LoginUserVo getLoginUserVo(User user);
}
