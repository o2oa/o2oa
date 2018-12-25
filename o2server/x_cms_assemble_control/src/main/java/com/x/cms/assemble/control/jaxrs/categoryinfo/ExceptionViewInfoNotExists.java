package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionViewInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewInfoNotExists( String id ) {
		super("ID为{}的数据视图信息不存在。", id );
	}
}
