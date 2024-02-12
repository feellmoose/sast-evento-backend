package sast.evento.annotation;

import sast.evento.common.enums.Auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    /* 添加注释后,默认为管理员操作 */
    Auth value() default Auth.ADMIN;
    String group() default "default";
}
