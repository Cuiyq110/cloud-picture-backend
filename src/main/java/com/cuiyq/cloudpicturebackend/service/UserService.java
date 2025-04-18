package com.cuiyq.cloudpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cuiyq.cloudpicturebackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cuiyq.cloudpicturebackend.model.dto.user.UserQueryRequest;
import com.cuiyq.cloudpicturebackend.model.vo.LoginUserVo;
import com.cuiyq.cloudpicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author cuiyq
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-04-16 16:12:40
*/
public interface UserService extends IService<User> {


    /**
     * 获取查询条件
     * @param userQueryRequest 查询请求体
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 用户退出
     * @param request 请求
     * @return 是否退出成功
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取当前登录用户信息
     * @param request
     * @return 用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    User getLoginUser();

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
     * 根据用户获取脱敏后的单个用户信息
     * @param user 原始用户信息
     * @return 脱敏后的用户信息
     */
    UserVO getUserVo(User user);

    /**
     * 根据用户列表获取脱敏后的用户列表信息
     * @param user 原始用户信息
     * @return 脱敏后的用户信息
     */
    List<UserVO> getUserVoList(List<User> user);
    /**
     * 获取脱敏后的用户信息
     * @param user 原始用户信息
     * @return 脱敏后的用户信息
     */
    LoginUserVo getLoginUserVo(User user);
}
