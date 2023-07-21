package com.x.attendance.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class StatisticYearEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	StatisticYearEmptyException() {
		super("统计年份不能为空.");
	}
}
