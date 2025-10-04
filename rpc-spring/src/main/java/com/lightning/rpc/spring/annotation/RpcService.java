package com.lightning.rpc.spring.annotation;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {
    
    
    String version() default "1.0.0";
    
    
    String group() default "default";
    
    
    int weight() default 100;
}
