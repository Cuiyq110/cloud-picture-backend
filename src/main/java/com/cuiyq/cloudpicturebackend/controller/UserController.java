package com.cuiyq.cloudpicturebackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuiyq.cloudpicturebackend.aop.AuthCheck;
import com.cuiyq.cloudpicturebackend.constant.UserConstant;
import com.cuiyq.cloudpicturebackend.exception.BusinessException;
import com.cuiyq.cloudpicturebackend.exception.ErrorCode;
import com.cuiyq.cloudpicturebackend.exception.ThrowUtils;
import com.cuiyq.cloudpicturebackend.model.domain.User;
import com.cuiyq.cloudpicturebackend.model.dto.user.*;
import com.cuiyq.cloudpicturebackend.model.vo.LoginUserVo;
import com.cuiyq.cloudpicturebackend.model.vo.UserVO;
import com.cuiyq.cloudpicturebackend.service.impl.UserServiceImpl;
import com.cuiyq.cloudpicturebackend.common.BaseResponse;
import com.cuiyq.cloudpicturebackend.common.DeleteRequest;
import com.cuiyq.cloudpicturebackend.common.ResultUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * 管理员 分页获取用户列表（脱敏)
     * @param userQueryRequest 用户查询请求
     * @return
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 1.提取用户查询请求中的当前页码和页面大小
        int current = userQueryRequest.getCurrent();
        int pageSize = userQueryRequest.getPageSize();
        // 2.创建一个User类型的Page对象，用于后续的分页查询
        Page<User> userPage = new Page<>(current,pageSize);
        //3.获取分页数据 user
        Page<User> userPageResult = userService.page(userPage, userService.getQueryWrapper(userQueryRequest));
        //4.数据脱敏：将 List<User> 转换为 List<UserVO>
        List<User> userList = userPageResult.getRecords();
        List<UserVO> userVoList = userService.getUserVoList(userList);
        //        获取新的分页结果
        Page<UserVO> userVOPage = new Page<>(current, pageSize,userPage.getTotal());
        // 4. 将脱敏后的数据设置到新分页对象中
        userVOPage.setRecords(userVoList);
        //5.返回结果
        return ResultUtils.success(userVOPage);
    }


    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(userUpdateRequest == null || userUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 删除用户
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(b);
    }

    /**
     * 根据id获取包装类(脱敏)
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        BaseResponse<User> userBaseResponse = getUserById(id);
        User data = userBaseResponse.getData();
        return ResultUtils.success(userService.getUserVo(data));
    }

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
