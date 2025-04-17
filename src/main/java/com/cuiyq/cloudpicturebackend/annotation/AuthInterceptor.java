package com.cuiyq.cloudpicturebackend.annotation;

import com.cuiyq.cloudpicturebackend.aop.AuthCheck;
import com.cuiyq.cloudpicturebackend.exception.ErrorCode;
import com.cuiyq.cloudpicturebackend.exception.ThrowUtils;
import com.cuiyq.cloudpicturebackend.model.domain.User;
import com.cuiyq.cloudpicturebackend.model.enums.UserRoleEnum;
import com.cuiyq.cloudpicturebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.cuiyq.cloudpicturebackend.constant.UserConstant.KEY_PRE_FIX;
import static com.cuiyq.cloudpicturebackend.constant.UserConstant.USER_LOGIN_KEY;

/**
 * desc
 *
 * @author cuiyq
 * @version 1.0
 * @since 2025/04/17 10:42
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    /**
     * 执行拦截，进行权限校验
     *
     * @param proceedingJoinPoint 切入点
     * @param authCheck           权限校验注解
     * @return 继续放行
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint proceedingJoinPoint, AuthCheck authCheck) throws Throwable {
//        获得必须需要的权限
        String mustRole = authCheck.mustRole();
//        必须要有的权限枚举值
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
//        1.获得当前登录用户
        User loginUser = userService.getLoginUser();

//        2.不需要权限放行
        if (mustRoleEnum == null) {
            return proceedingJoinPoint.proceed();
        }
//        3.必须有权限才放行
//        3.1获取当前用户的权限
        String userRole = loginUser.getUserRole();
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(userRole);
//        3.2 没有权限拒接
        ThrowUtils.throwIf(userRoleEnum == null, ErrorCode.NO_AUTH_ERROR, "无权限");
//        3.3 必须要要求有管理员权限，但用户没有管理员权限
        ThrowUtils.throwIf(mustRoleEnum.equals(UserRoleEnum.ADMIN) && !userRoleEnum.equals(UserRoleEnum.ADMIN), ErrorCode.NO_AUTH_ERROR, "无权限");
//        3.4通过，放行
        return proceedingJoinPoint.proceed();
    }

}
