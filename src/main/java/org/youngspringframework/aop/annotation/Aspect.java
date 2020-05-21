package org.youngspringframework.aop.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    // 需要被织入横切逻辑的注解标签
    Class<? extends Annotation> value();
}
