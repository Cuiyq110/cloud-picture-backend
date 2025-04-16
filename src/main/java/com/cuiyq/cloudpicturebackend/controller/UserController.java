package com.cuiyq.cloudpicturebackend.controller;

import com.cuiyq.cloudpicturebackend.exception.BusinessException;
import com.cuiyq.cloudpicturebackend.exception.ErrorCode;
import com.cuiyq.cloudpicturebackend.exception.ThrowUtils;
import com.cuiyq.cloudpicturebackend.model.domain.User;
import com.cuiyq.cloudpicturebackend.model.dto.user.UserLoginRequest;
import com.cuiyq.cloudpicturebackend.model.dto.user.UserRegisterRequest;
import com.cuiyq.cloudpicturebackend.model.vo.UserLoginVo;
import com.cuiyq.cloudpicturebackend.service.impl.userServiceImpl;
import com.cuiyq.common.BaseResponse;
import com.cuiyq.common.ResultUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
    private userServiceImpl userService;

    /**
     * 用户登录
     * @param UserLoginRequest 登录请求类
     * @return 登录结果
     */
    @PostMapping("/login")
    public BaseResponse<UserLoginVo> userLogin(@RequestBody UserLoginRequest UserLoginRequest) {
//        如果为空返回异常
        ThrowUtils.throwIf(UserLoginRequest == null, ErrorCode.PARAMS_ERROR);
//        获取账号密码
        String userAccount = UserLoginRequest.getUserAccount();
        String userPassword = UserLoginRequest.getUserPassword();
//        登录
        UserLoginVo userLoginVo = userService.userLogin(userAccount, userPassword);
        ThrowUtils.throwIf(userLoginVo == null, ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        return ResultUtils.success(userLoginVo);
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
