package com.cuiyq.cloudpicturebackend.service.impl;

import java.util.Date;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuiyq.cloudpicturebackend.exception.BusinessException;
import com.cuiyq.cloudpicturebackend.exception.ErrorCode;
import com.cuiyq.cloudpicturebackend.exception.ThrowUtils;
import com.cuiyq.cloudpicturebackend.model.domain.User;
import com.cuiyq.cloudpicturebackend.model.enums.UserRoleEnum;
import com.cuiyq.cloudpicturebackend.service.userService;
import com.cuiyq.cloudpicturebackend.mapper.userMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

/**
 * @author cuiyq
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-04-16 16:12:40
 */
@Service
public class userServiceImpl extends ServiceImpl<userMapper, User>
        implements userService {


    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return 新用户id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
//        1.校验参数
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

//        2.检查账户是否已经存到数据库中
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", userPassword);
        long count = baseMapper.selectCount(queryWrapper);
        //如果存在证明用户已经存在
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "用户已存在");
//        3.密码一定要加密
        String encryptByPassword = getEncryptByPassword(userPassword);
//        4.插入数据到数据库中
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptByPassword);
        user.setUserName("无名");
        String value = UserRoleEnum.USER.getValue();
        user.setUserRole(value);
        baseMapper.insert(user);
//      5.返回数据
        return user.getId();
    }

    /**
     * 密码加密
     *
     * @param userPassword 密码
     * @return 加密后的密码
     */
    @Override
    public String getEncryptByPassword(String userPassword) {
        final String salt = "cuiyq";
//        加盐，返回加密后的密码
        return DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
    }
}




