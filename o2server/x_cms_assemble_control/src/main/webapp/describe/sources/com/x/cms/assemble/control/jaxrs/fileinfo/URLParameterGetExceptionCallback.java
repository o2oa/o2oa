package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.CallbackPromptException;

public class URLParameterGetExceptionCallback extends CallbackPromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public URLParameterGetExceptionCallback(String callbackName, Throwable e ) {
		super(callbackName, "系统在解析传入的URL参数时发生异常.", e);
	}
}
