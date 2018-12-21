package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JpaObject;

public enum AvailableMode {
	/**
	 * 启动方式:authenticated 认证过的用户, assign 指定人员, none 无人可以启动
	 */
	authenticated, assign, none;

	public static final int length = JpaObject.length_16B;
}
