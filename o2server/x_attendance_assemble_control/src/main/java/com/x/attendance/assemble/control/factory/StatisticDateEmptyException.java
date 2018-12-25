package com.x.attendance.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class StatisticDateEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	StatisticDateEmptyException() {
		super("统计日期不能为空.");
	}
}
