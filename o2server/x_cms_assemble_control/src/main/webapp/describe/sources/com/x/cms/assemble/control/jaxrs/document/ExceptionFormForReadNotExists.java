package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.PromptException;

class ExceptionFormForReadNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionFormForReadNotExists( String id ) {
		super("文档阅读表单不存在。ID:" + id );
	}
}
