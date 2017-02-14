package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JpaObject;

/*定义流转操作*/
public enum ProcessingType {
	/* 继续流转 */
	processing,
	/* 开始 */
	start,
	/* 调度 */
	reroute,
	/* 召回 */
	retract,
	/* 重置处理人 */
	reset,
	/* 管理员流转 */
	control;
	public static final int length = JpaObject.length_16B;
}
