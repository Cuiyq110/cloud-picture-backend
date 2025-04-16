package com.cuiyq.cloudpicturebackend.constant;

/**
 * desc redis key 常量
 *
 * @author cuiyq
 * @version 1.0
 * @since 2025/04/16 17:37
 */
public interface RedisConstant {
    String USER_LOGIN_KEY = "cloude-picture-backend:user:login:";
    Long USER_LOGIN_EXPIRE_SECONDS = 60 * 60 * 24 * 7L;

}
