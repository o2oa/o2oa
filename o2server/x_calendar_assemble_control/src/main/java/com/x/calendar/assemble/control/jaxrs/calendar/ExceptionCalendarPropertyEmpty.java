package com.x.calendar.assemble.control.jaxrs.calendar;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarPropertyEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCalendarPropertyEmpty( String name ) {
		super("操作传入的日历属性'"+name+"'为空，无法进行保存操作.");
	}
}
