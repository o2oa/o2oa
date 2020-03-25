package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionMessageProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionMessageProcess( Throwable e, String message ) {
		super("系统进行日历事件消息息处理时发生异常！message:" + message, e );
	}
}
