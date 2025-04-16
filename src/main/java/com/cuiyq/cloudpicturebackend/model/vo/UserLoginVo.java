package com.cuiyq.cloudpicturebackend.model.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * desc : 用户登录VO
 *
 * @author cuiyq
 * @version 1.0
 * @since 2025/04/16 17:21
 */
@Data
public class UserLoginVo implements Serializable {

    private static final long serialVersionUID = 8662420115990750759L;
    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;
}

