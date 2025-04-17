package com.cuiyq.cloudpicturebackend.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuiyq.cloudpicturebackend.exception.BusinessException;
import com.cuiyq.cloudpicturebackend.exception.ErrorCode;
import com.cuiyq.cloudpicturebackend.exception.ThrowUtils;
import com.cuiyq.cloudpicturebackend.model.domain.User;
import com.cuiyq.cloudpicturebackend.model.enums.UserRoleEnum;
import com.cuiyq.cloudpicturebackend.model.vo.LoginUserVo;
import com.cuiyq.cloudpicturebackend.service.UserService;
import com.cuiyq.cloudpicturebackend.mapper.userMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.cuiyq.cloudpicturebackend.constant.UserConstant.*;

/**
 * @author cuiyq
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-04-16 16:12:40
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<userMapper, User>
        implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private String tokenKey = "";

    /**
     * 用户注销
     * @param request 请求
     * @return 是否注销成功
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
//        1.先判断是否已登录
        //判断是否登录
        Object o = stringRedisTemplate.opsForHash().get(tokenKey, "id");
        ThrowUtils.throwIf(o == null, ErrorCode.PARAMS_ERROR, "用户未登录");
//        2.移除登录态
        Long id = stringRedisTemplate.opsForHash().delete(tokenKey, "id");
        return id > 0;
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request
     * @return 用户信息
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
//        判断是否登录
        Object o = stringRedisTemplate.opsForHash().get(tokenKey, "id");
        ThrowUtils.throwIf(o == null, ErrorCode.PARAMS_ERROR, "用户未登录");
//        根据缓存中的id查数据库
            String userId = o.toString();
            User user = query().eq("id", userId).one();
            return user;
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @Override
    public User getLoginUser() {

//        判断是否登录
        Object o = stringRedisTemplate.opsForHash().get(tokenKey, "id");
        ThrowUtils.throwIf(o == null, ErrorCode.PARAMS_ERROR, "用户未登录");
//        根据缓存中的id查数据库
        String userId = o.toString();
        User user = query().eq("id", userId).one();
        return user;
    }


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
//        TODO 用户头像，设置默认头像
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

    @Override
    public LoginUserVo userLogin(String userAccount, String userPassword) {
//        1.校验
        // 1. 校验
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码错误");
        }
//        2.对用户传递的密码进行加密
        String encryptByPassword = getEncryptByPassword(userPassword);
//        3.查询数据库，判断用户是否存在
        User user = query().eq("userAccount", userAccount)
                .eq("userPassword", encryptByPassword)
                .one();
        if (user == null) {
            //4.不存在抛异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

//        5.保存用户信息，到redis中
//        5.1 随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString();
        // 5.2. 将User对象转为HashMap存储
        Map<String, Object> userMap = BeanUtil.beanToMap(user, new HashMap<>(), CopyOptions.create()
                .setIgnoreNullValue(true)
                .setFieldValueEditor((fieldName, fieldValue) -> {
                    if (fieldValue instanceof Date) {
                        return DateUtil.format((Date) fieldValue, "yyyy-MM-dd HH:mm:ss");
                    }
                    return fieldValue != null ? fieldValue.toString() : null;
                }));
        // 5.3.存储
        tokenKey = KEY_PRE_FIX + USER_LOGIN_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);

        //5.4.设置token有效期
        stringRedisTemplate.expire(tokenKey, USER_LOGIN_EXPIRE_SECONDS, TimeUnit.SECONDS);
//        6.返回脱敏用户信息
        return getLoginUserVo(user);
    }

    /**
     * 获取脱敏类的用户信息
     *
     * @param user 用户实体类，包含用户的基本信息
     * @return UserLoginVo 用户登录信息的视图对象，包含用户登录所需的信息
     */
    @Override
    public LoginUserVo getLoginUserVo(User user) {
        // 检查传入的用户对象是否为空，为空则返回null，表示没有用户信息
        if (user == null) {
            return null;
        }
        // 创建一个用户登录信息的视图对象实例
        LoginUserVo loginUserVo = new LoginUserVo();
        // 使用BeanUtil工具类将用户实体类的属性值复制到用户登录信息的视图对象中
        // 这里使用工具类是为了简化代码，提高开发效率
        BeanUtil.copyProperties(user, loginUserVo);
        // 返回填充好用户信息的视图对象
        return loginUserVo;
    }
}




