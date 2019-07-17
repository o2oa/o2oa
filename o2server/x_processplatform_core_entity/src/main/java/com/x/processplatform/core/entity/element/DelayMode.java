package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JpaObject;

public enum DelayMode {
	/* 定义的时候使用了 @Enumerated(EnumType.ORDINAL),这里的顺序不能修改 */
	until, minute;
	public static final int length = JpaObject.length_16B;
}