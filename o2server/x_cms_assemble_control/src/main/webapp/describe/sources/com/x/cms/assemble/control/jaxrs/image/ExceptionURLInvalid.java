package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.project.exception.PromptException;

class ExceptionURLInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionURLInvalid() {
		super("图片地址URL不合法,无法获取互联网图片信息。" );
	}
}
