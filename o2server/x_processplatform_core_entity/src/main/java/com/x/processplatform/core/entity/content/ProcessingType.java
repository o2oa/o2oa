package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JpaObject;

/**
 * 
 * @author zhour 定义流转的操作 在 TaskCompleted 和WorkLog中记录操作类型用于判断
 */
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
	/* 智能流转 */
	sameTarget,
	/* 超时流转 */
	expire,
	/* 管理员流转 */
	control,
	/* 添加处理人 */
	appendTask;

	public static final int length = JpaObject.length_16B;
}
