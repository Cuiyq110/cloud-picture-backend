package com.cuiyq.cloudpicturebackend.controller;

import com.cuiyq.cloudpicturebackend.exception.BusinessException;
import com.cuiyq.cloudpicturebackend.exception.ErrorCode;
import com.cuiyq.cloudpicturebackend.exception.ThrowUtils;
import com.cuiyq.cloudpicturebackend.model.domain.User;
import com.cuiyq.cloudpicturebackend.model.dto.user.UserRegisterRequest;
import com.cuiyq.cloudpicturebackend.service.impl.userServiceImpl;
import com.cuiyq.common.BaseResponse;
import com.cuiyq.common.ResultUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 用户注册
     *
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
