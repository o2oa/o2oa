package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoNotExists( String id ) {
		super("指定的应用不存在:{}.", id );
	}
}
