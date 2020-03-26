package com.x.calendar.assemble.control.jaxrs.setting;

import com.x.base.core.project.exception.PromptException;

class ExceptionSettingInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionSettingInfoProcess( Throwable e, String message ) {
		super("用户在进行设置信息处理时发生异常！message:" + message, e );
	}
}
