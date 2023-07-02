package com.x.calendar.assemble.control.jaxrs.calendar;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarTargetInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCalendarTargetInvalid( String name ) {
		super("操作传入的日历目标者标识'"+name+"'不合法，需要为人员标识或者组织标识.");
	}
}
