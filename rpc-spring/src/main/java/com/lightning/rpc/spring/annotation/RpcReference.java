package com.lightning.rpc.spring.annotation;

import java.lang.annotation.*;


@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcReference {
    
    
    String version() default "1.0.0";
    
    
    String group() default "default";
    
    
    int timeout() default 5000;
}
