package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionEventProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEventProcess( Throwable e, String message ) {
		super("系统进行日历事件信息处理时发生异常！message:" + message, e );
	}
	
	ExceptionEventProcess( String message ) {
		super("系统进行日历事件信息处理时发生异常！message:" + message );
	}
}
