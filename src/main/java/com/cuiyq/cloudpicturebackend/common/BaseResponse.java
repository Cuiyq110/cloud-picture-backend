package com.cuiyq.cloudpicturebackend.common;

import com.cuiyq.cloudpicturebackend.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * desc 全局响应封装类
 *
 * @author cuiyq
 * @version 1.0
 * @since 2025/04/16 11:31
 */
@Data
public class BaseResponse<T> implements Serializable {


    private int code;
    private T data;
    private String message;

    /**
     * 返回描述
     * @param code
     * @param data
     * @param message
     */
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    /**
     * 成功信息,不返回描述
     * @param code
     * @param data
     */
    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    /**
     * 异常信息
     * @param errorCode
     */
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(),null, errorCode.getMessage());
    }
}
