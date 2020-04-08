package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionEventAlarmConfigError extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEventAlarmConfigError() {
		super("事件提醒时间配置不合法，请检查提醒设置。");
	}
	
	ExceptionEventAlarmConfigError( String message ) {
		super("事件提醒时间配置不合法，" + message );
	}
}
