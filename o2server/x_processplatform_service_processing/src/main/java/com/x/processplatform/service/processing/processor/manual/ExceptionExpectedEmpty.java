package com.x.processplatform.service.processing.processor.manual;

import com.x.base.core.project.exception.RunningException;

@Deprecated(since = "8.2")
class ExceptionExpectedEmpty extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionExpectedEmpty(String title, String id, String activityName, String activityId) {
		super("预计的处理人为空, 标题:{}, 标识:{}, 活动:{}, 活动标识:{}, 记录的处理人:{}", title, id, activityName, activityId);
	}

}
