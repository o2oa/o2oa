package com.x.report.assemble.control.jaxrs.setting.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSettingInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSettingInfoProcess( Throwable e, String message ) {
		super("用户在进行设置信息处理时发生异常！message:" + message, e );
	}
}
