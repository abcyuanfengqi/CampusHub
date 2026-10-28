package com.campushub.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色权限注解
 * 标注在 Controller 方法上，要求当前用户属于指定角色之一
 * 例：@RequireRole("ADMIN") / @RequireRole({"ADMIN","CLUB_LEADER"})
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String[] value();
}
