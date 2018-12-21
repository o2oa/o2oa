package com.x.attendance.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class StatisticMonthEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	StatisticMonthEmptyException() {
		super("统计月份不能为空.");
	}
}
