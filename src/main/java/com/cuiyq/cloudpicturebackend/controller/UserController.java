package com.cuiyq.cloudpicturebackend.controller;

import com.cuiyq.cloudpicturebackend.aop.AuthCheck;
import com.cuiyq.cloudpicturebackend.constant.UserConstant;
import com.cuiyq.cloudpicturebackend.exception.BusinessException;
import com.cuiyq.cloudpicturebackend.exception.ErrorCode;
import com.cuiyq.cloudpicturebackend.exception.ThrowUtils;
import com.cuiyq.cloudpicturebackend.model.domain.User;
import com.cuiyq.cloudpicturebackend.model.dto.user.UserAddRequest;
import com.cuiyq.cloudpicturebackend.model.dto.user.UserLoginRequest;
import com.cuiyq.cloudpicturebackend.model.dto.user.UserRegisterRequest;
import com.cuiyq.cloudpicturebackend.model.vo.LoginUserVo;
import com.cuiyq.cloudpicturebackend.service.impl.UserServiceImpl;
import com.cuiyq.common.BaseResponse;
import com.cuiyq.common.ResultUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * desc
 *
 * @author cuiyq
 * @version 1.0
 * @since 2025/04/16 16:40
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserServiceImpl userService;


    /**
     * 添加用户
     * @param userAddRequest 添加用户请求类
     * @return 添加结果
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
//        1.设置默认密码
        final String DEFAULT_PASSWORD = "12345678";
        String encryptByPassword = userService.getEncryptByPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptByPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 管理员根据id查询用户未脱敏
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
//        3.TODO redis缓存
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }


    /**
     * 用户注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        boolean b = userService.userLogout(request);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(b);
    }

    @GetMapping("/get/login")
    public BaseResponse<LoginUserVo> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
//        返回脱敏后的LoginUserVo对象
        return ResultUtils.success(userService.getLoginUserVo(user));

    }

    /**
     * 用户登录
     * @param UserLoginRequest 登录请求类
     * @return 登录结果
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVo> userLogin(@RequestBody UserLoginRequest UserLoginRequest) {
//        如果为空返回异常
        ThrowUtils.throwIf(UserLoginRequest == null, ErrorCode.PARAMS_ERROR);
//        获取账号密码
        String userAccount = UserLoginRequest.getUserAccount();
        String userPassword = UserLoginRequest.getUserPassword();
//        登录
        LoginUserVo loginUserVo = userService.userLogin(userAccount, userPassword);
        ThrowUtils.throwIf(loginUserVo == null, ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        return ResultUtils.success(loginUserVo);
    }


    /**
     * 用户注册
     * @param userRegisterRequest 注册请求类
     * @return 注册结果
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long l = userService.userRegister(userAccount, userPassword, checkPassword);

        return ResultUtils.success(l);
    }
}
