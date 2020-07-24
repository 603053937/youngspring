package org.youngspringframework.aop.annotation;

import java.lang.annotation.*;

//作用于类上
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    String pointcut();
}
