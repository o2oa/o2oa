package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.PromptException;

class ExceptionViewNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewNotExists( String id ) {
		super("数据视图不存在，无法继续进行操作。Id:" + id );
	}
}
