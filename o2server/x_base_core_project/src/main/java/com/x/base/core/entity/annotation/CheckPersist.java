package com.x.base.core.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface CheckPersist {

	boolean allowEmpty() default true;

	boolean allowContainEmpty() default true;

	boolean simplyString() default false;

	boolean fileNameString() default false;

	boolean mailString() default false;

	boolean mobileString() default false;

	CitationExist[] citationExists() default {};

	CitationNotExist[] citationNotExists() default {};

	String pattern() default "";

	String min() default "";

	String max() default "";

	String[] excludes() default {};

}