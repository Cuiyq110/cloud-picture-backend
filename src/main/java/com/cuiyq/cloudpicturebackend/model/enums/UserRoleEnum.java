package com.cuiyq.cloudpicturebackend.model.enums;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * desc 根据value获取枚举
 * @author cuiyq
 * @version 1.0
 * @since 2025/04/16 09:25
 */
@Getter
public enum UserRoleEnum {
    USER("用户", "user"),
    ADMIN("管理员", "admin");

    private final String text;

    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static UserRoleEnum getEnumByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        /**
         * 创建一个Map，key为枚举值的value，value为枚举对象
         */
        Map<String, UserRoleEnum> userRoleEnumMap = Arrays.stream(UserRoleEnum.values()).
                collect(Collectors.toMap(UserRoleEnum::getValue, userRoleEnum -> userRoleEnum));
        return userRoleEnumMap.getOrDefault(value, null);

    }
}

