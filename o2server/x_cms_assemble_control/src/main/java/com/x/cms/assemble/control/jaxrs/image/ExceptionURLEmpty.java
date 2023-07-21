package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.project.exception.PromptException;

class ExceptionURLEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionURLEmpty() {
		super("URL为空，无法进行查询操作。" );
	}
}
