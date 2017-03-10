package com.x.okr.assemble.control.servlet.workimport;

import com.x.base.core.exception.PromptException;

class WorkCompleteDateLimitFormatException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkCompleteDateLimitFormatException( Throwable e, String date ) {
		super("工作完成时限格式不正确，无法进行工作保存。Date：" + date, e );
	}
}
