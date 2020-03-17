package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.project.exception.PromptException;

class ExceptionURLParameterGet extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	ExceptionURLParameterGet(Throwable e ) {
		super("系统在解析传入的URL参数时发生异常.", e);
	}
}
