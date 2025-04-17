package com.cuiyq.cloudpicturebackend.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * desc 
 * @author cuiyq
 * @version 1.0
 * @since 2025/04/17 10:41
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 必须有某个角色才可访问
     * @return
     */
    String mustRole() default "";
}
