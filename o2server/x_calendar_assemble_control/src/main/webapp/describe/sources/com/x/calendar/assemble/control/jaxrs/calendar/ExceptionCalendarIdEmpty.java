package com.x.calendar.assemble.control.jaxrs.calendar;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCalendarIdEmpty() {
		super("查询操作传入的参数日历Id为空，无法进行查询操作.");
	}
}
