package com.cuiyq.cloudpicturebackend.constant;

/**
 * desc
 *
 * @author cuiyq
 * @version 1.0
 * @since 2025/04/16 21:01
 */
public interface UserConstant {

    String USER_LOGIN_KEY = "user:login:";
    Long USER_LOGIN_EXPIRE_SECONDS = 60 * 60 * 24 * 7L;
    String USER_LOGIN_CODE_KEY = "user:login:code:";
    String KEY_PRE_FIX = "cloud-picture-backend:";


    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    // endregion

}
