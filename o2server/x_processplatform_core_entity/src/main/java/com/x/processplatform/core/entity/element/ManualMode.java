package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JpaObject;

/**
 * 节点方式，单人活动，并行或者顺序执行
 */
public enum ManualMode {
	single, parallel, queue;
	
	public static final int length=JpaObject.length_16B;
}