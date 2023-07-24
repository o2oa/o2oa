package com.x.attendance.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class RecordDateEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RecordDateEmptyException() {
		super("打卡日期不能为空.");
	}
}
