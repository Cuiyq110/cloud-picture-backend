package com.cuiyq.cloudpicturebackend.controller;

import com.cuiyq.cloudpicturebackend.aop.AuthCheck;
import com.cuiyq.cloudpicturebackend.common.BaseResponse;
import com.cuiyq.cloudpicturebackend.common.ResultUtils;
import com.cuiyq.cloudpicturebackend.constant.UserConstant;
import com.cuiyq.cloudpicturebackend.exception.BusinessException;
import com.cuiyq.cloudpicturebackend.exception.ErrorCode;
import com.cuiyq.cloudpicturebackend.manager.CosManager;
import com.cuiyq.cloudpicturebackend.model.enums.UserRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * desc 文件请求接口
 *
 * @author cuiyq
 * @version 1.0
 * @since 2025/04/20 12:14
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private CosManager cosManager;

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testuploadFile(@RequestPart("file")MultipartFile multipartFile) {

//        1.接收文件 文件目录
        String fileName = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%s", fileName);
//        2.将文件存储在腾讯中
        File tempFile = null;
        try {
            tempFile = File.createTempFile(filepath, null);
            multipartFile.transferTo(tempFile);
            cosManager.putObject(filepath, tempFile);
            //        3.返回文件地址
            return ResultUtils.success(filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath= " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"上传失败");
        } finally {
            if (tempFile != null) {
                boolean delete = tempFile.delete();
                if (!delete) {
                    log.error("file delete error, filepath= " + filepath);
                }
            }
        }
    }
}
