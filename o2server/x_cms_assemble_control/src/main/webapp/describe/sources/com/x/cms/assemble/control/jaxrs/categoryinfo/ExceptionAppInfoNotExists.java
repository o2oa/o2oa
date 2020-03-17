package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoNotExists( String id ) {
		super("ID为{}的栏目信息不存在。", id );
	}
}
