package com.x.calendar.assemble.control.jaxrs.calendar;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCalendarNotExists( String id ) {
		super("指定的日历信息不存在.ID:" + id );
	}
}
