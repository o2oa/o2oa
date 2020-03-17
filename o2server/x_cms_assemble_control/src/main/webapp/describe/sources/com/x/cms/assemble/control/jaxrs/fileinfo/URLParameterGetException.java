package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

public class URLParameterGetException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public URLParameterGetException(Throwable e ) {
		super("系统在解析传入的URL参数时发生异常.", e);
	}
}
