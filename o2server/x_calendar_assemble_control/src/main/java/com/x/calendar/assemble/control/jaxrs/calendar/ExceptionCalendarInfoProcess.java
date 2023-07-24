package com.x.calendar.assemble.control.jaxrs.calendar;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCalendarInfoProcess( Throwable e, String message ) {
		super("用户在进行日历信息处理时发生异常！message:" + message, e );
	}
	
	ExceptionCalendarInfoProcess( String message ) {
		super("用户在进行日历信息处理时发生异常！message:" + message );
	}
}
