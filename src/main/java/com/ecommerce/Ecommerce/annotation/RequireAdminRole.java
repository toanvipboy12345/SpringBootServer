package com.ecommerce.Ecommerce.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequireAdminRole {
    // Mảng các vai trò admin được phép truy cập
    String[] roles() default {"super_admin"}; // Mặc định là super_admin
}