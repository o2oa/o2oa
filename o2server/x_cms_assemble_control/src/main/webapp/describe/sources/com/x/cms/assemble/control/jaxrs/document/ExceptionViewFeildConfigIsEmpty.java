package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.PromptException;

class ExceptionViewFeildConfigIsEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewFeildConfigIsEmpty( String id ) {
		super("列表未设置任何可用列，无法继续进行操作。Id:" + id );
	}
}
