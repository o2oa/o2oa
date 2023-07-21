package com.x.base.core.project.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//@java.lang.annotation.Target(value={java.lang.annotation.ElementType.PARAMETER,java.lang.annotation.ElementType.METHOD,java.lang.annotation.ElementType.FIELD})
//@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface FieldDescribe {
	abstract String value();
}