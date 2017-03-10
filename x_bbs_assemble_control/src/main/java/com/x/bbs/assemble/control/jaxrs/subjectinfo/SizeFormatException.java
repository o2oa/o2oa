package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class SizeFormatException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SizeFormatException( String size ) {
		super("请求参数size格式不合法, 要求为数字.Size:" + size );
	}
}
