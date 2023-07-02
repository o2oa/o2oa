package com.x.base.core.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识Entity,由于@Entity标识可能扫描到其他的类,所以这里单独用一个标识,表示是自建的类,这样可以在scan 的时候区分
 * 
 * 
 * @author zhour
 * 
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface ContainerEntity {

	/**
	 * 执行dump时分组数量
	 */
	public int dumpSize() default 200;

	/**
	 * 类型
	 */
	public Type type() default Type.content;

	/**
	 * 引用,强数据,软数据(软数据不会影响系统运行)
	 */

	public Reference reference() default Reference.strong;

	public enum Type {
		element, content, log, custom;
	}

	public enum Reference {
		strong, soft;
	}
}
