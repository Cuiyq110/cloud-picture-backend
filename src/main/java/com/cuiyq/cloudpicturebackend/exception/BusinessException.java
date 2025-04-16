package com.cuiyq.cloudpicturebackend.exception;

import lombok.Getter;

/**
 * desc 自定义业务异常
 * @author cuiyq
 * @version 1.0
 * @since 2025/04/16 09:25
 */
@Getter
public class BusinessException extends RuntimeException {

  /**
   * 错误码
   */
  private final int code;

  public BusinessException(int code, String message) {
    super(message);
    this.code = code;
  }

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.code = errorCode.getCode();
  }

  public BusinessException(ErrorCode errorCode,String message) {
    super(message);
    this.code = errorCode.getCode();
  }


}
