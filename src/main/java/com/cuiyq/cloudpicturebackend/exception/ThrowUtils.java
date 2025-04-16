package com.cuiyq.cloudpicturebackend.exception;

/**
 * desc
 *
 * @author cuiyq
 * @version 1.0
 * @since 2025/04/16 09:36
 */
public class ThrowUtils {


    /**
     * 条件成立则抛出异常 运行时异常
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛出异常
     * @param condition 条件
     * @param errorCode 错误码
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        if (condition) {
            throwIf(condition,new BusinessException(errorCode));
        }
    }

    /**
     * 条件成立则抛出异常
     * @param condition 条件
     * @param errorCode 错误码
     * @param message 自定义错误消息
     */

    public static void throwIf(boolean condition,ErrorCode errorCode, String message) {
        throwIf(condition,new BusinessException(errorCode,message));
    }

}
