package com.x.jpush.assemble.control.jaxrs.sample;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSampleEntityClassProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSampleEntityClassProcess( Throwable e, String message ) {
		super("用户在进行设置信息处理时发生异常！message:" + message, e );
	}
}
