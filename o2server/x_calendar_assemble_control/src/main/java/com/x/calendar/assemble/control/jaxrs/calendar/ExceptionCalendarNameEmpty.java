package com.x.calendar.assemble.control.jaxrs.calendar;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCalendarNameEmpty() {
		super("操作传入的日历名称name为空，无法进行查询或者保存操作.");
	}
}
