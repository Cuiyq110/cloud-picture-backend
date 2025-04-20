package com.cuiyq.cloudpicturebackend.controller;

import com.cuiyq.cloudpicturebackend.common.BaseResponse;
import com.cuiyq.cloudpicturebackend.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc
 *
 * @author cuiyq
 * @version 1.0
 * @since 2025/04/16 11:59
 */
@RestController
@RequestMapping("/")
public class MainController {

    /**
     * 健康检查
     *
     * @return
     */
    @GetMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}
