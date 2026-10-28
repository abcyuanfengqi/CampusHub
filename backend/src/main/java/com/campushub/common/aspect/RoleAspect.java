package com.campushub.common.aspect;

import com.campushub.common.annotation.RequireRole;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.utils.UserContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 角色权限切面：校验 @RequireRole 标注的方法调用者角色
 * 满足 C1 复杂度（≥3 类角色权限控制）
 */
@Aspect
@Component
public class RoleAspect {

    @Before("@annotation(requireRole)")
    public void checkRole(RequireRole requireRole) {
        String currentRole = UserContext.getRole();
        String[] allowed = requireRole.value();
        boolean pass = Arrays.asList(allowed).contains(currentRole);
        if (!pass) {
            throw new BusinessException("无权限执行该操作（需要角色: " + String.join("/", allowed) + "）");
        }
    }
}
