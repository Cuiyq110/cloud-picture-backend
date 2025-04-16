package com.cuiyq.cloudpicturebackend.service;

import com.cuiyq.cloudpicturebackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author cuiyq
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-04-16 16:12:40
*/
public interface userService extends IService<User> {

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


}
