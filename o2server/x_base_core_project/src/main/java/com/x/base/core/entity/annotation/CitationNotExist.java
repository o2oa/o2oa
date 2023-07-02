package com.x.base.core.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.x.base.core.entity.JpaObject;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CitationNotExist {

	Class<? extends JpaObject> type();

	String[] fields() default { "id" };

	Equal[] equals() default {};

	NotEqual[] notEquals() default {};

}
