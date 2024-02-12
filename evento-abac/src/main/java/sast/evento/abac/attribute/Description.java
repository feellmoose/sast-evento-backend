package sast.evento.abac.attribute;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
    @AliasFor("type")
    Resource.Type value() default Resource.Type.NONE;
    @AliasFor("value")
    Resource.Type type() default Resource.Type.NONE;
    String description() default "none description";
}
