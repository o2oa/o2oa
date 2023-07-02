package com.x.processplatform.service.processing.processor.manual;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.RunningException;

class ExceptionChoiceRouteNameError extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionChoiceRouteNameError(List<String> taskCompletedIds) {
		super("路由选择错误,已办列表:{}.", StringUtils.join(taskCompletedIds, ","));
	}

}
