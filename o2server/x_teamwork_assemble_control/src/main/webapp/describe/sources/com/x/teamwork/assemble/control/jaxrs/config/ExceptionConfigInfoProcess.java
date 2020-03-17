package com.x.teamwork.assemble.control.jaxrs.config;

import com.x.base.core.project.exception.PromptException;

class ExceptionConfigInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionConfigInfoProcess( Throwable e, String message ) {
		super("用户在进行设置信息处理时发生异常！message:" + message, e );
	}
}
