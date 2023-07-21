package com.x.base.core.project.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface JaxrsMethodDescribe {

	String value();

	Class<? extends StandardJaxrsAction> action();

	DescribeScope scope() default DescribeScope.commonly;

}