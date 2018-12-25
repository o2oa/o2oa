package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCalendarNotExists( String id ) {
		super("日历信息不存在.ID:" + id );
	}
}
