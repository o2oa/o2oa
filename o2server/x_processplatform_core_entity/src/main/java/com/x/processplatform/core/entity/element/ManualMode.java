package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JpaObject;

/**
 * 节点方式:单人活动,并行,顺序执行,抢办
 */
public enum ManualMode {
	single, parallel, queue, grab;

	public static final int length = JpaObject.length_16B;
}