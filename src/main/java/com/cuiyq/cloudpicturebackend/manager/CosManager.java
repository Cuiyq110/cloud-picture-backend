package com.cuiyq.cloudpicturebackend.manager;

import com.cuiyq.cloudpicturebackend.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * desc 腾讯云manager
 *
 * @author cuiyq
 * @version 1.0
 * @since 2025/04/20 12:04
 */
@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private COSClient cosClient;

    // 将本地文件上传到 COS
    public PutObjectResult putObject(String key, File file)
            throws CosClientException, CosServiceException {

        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }


}
