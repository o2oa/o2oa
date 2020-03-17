package com.x.attendance.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class CycleYearEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CycleYearEmptyException() {
		super("统计年份不能为空.");
	}
}
