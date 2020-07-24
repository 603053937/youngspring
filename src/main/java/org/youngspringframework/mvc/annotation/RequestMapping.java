package org.youngspringframework.mvc.annotation;

import org.youngspringframework.mvc.type.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识Controller的方法与请求路径和请求方法的映射关系
 * 可以标记于类与方法上
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    //请求路径
    String value() default "";
    //请求方法,默认值设置为get
    RequestMethod method() default RequestMethod.GET;
}
