package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSizeFormat extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSizeFormat( String size ) {
		super("请求参数size格式不合法, 要求为数字.Size:" + size );
	}
}
