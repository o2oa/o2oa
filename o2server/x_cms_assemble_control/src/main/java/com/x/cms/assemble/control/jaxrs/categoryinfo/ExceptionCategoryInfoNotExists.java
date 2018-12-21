package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionCategoryInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryInfoNotExists( String id ) {
		super("ID为{}的分类信息不存在。", id );
	}
}
